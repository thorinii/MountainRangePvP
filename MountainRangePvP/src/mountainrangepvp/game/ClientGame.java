/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import javax.swing.JOptionPane;
import mountainrangepvp.Log;
import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.Client;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.mp.Proxy;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotListener;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class ClientGame extends Game {

    private HeightMap heightMap;
    //
    private final String playerName;
    private final String serverIP;
    private Client client;
    //
    private PlayerManager playerManager;
    private ShotManager shotManager;
    private PhysicsSystem physicsSystem;
    private InputHandler inputHandler;
    private AudioManager audioManager;
    //
    private GameScreen gameScreen;
    //
    private int playerUpdateTimer;

    public ClientGame(String playerName, String serverIP) {
        this.playerName = playerName;
        this.serverIP = serverIP;
    }

    @Override
    public void create() {
        try {
            Log.info("Connecting to", serverIP);

            client = new Client(serverIP, MultiplayerConstants.STD_PORT);
            client.getMessageQueue().addListener(new ServerMessageListener());
            client.start();
        } catch (IOException ioe) {
            Log.warn("Error starting server connection:", ioe);
            JOptionPane.showMessageDialog(null, "Error starting client " + ioe,
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        client.update();

        if (gameScreen != null) {
            inputHandler.update(dt);
            shotManager.update(dt);
            physicsSystem.update(dt);
            gameScreen.render(dt);

            playerUpdateTimer += (int) (1000 * dt);
            if (playerUpdateTimer > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
                client.send(new PlayerUpdateMessage(
                        playerManager.getLocalPlayer()));
                playerUpdateTimer = 0;
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        client.stop();

        System.exit(0);
    }

    private class ServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, Proxy proxy) throws IOException {
            if (message instanceof SeedMessage) {
                SeedMessage seedMessage = (SeedMessage) message;

                Log.info("Received Seed", seedMessage.getSeed(), "Changing Map");

                heightMap = new MountainHeightMap(seedMessage.getSeed());
                playerManager = new PlayerManager(playerName);
                shotManager = new ShotManager(heightMap, playerManager);
                physicsSystem = new PhysicsSystem(heightMap, playerManager);

                shotManager.addShotListener(new AddShotListener());

                inputHandler = new InputHandler(playerManager, shotManager);
                inputHandler.register();

                audioManager = new AudioManager(playerManager, shotManager);
                audioManager.loadAudio();

                gameScreen = new GameScreen(heightMap, playerManager,
                                            shotManager);
                setScreen(gameScreen);
            } else if (message instanceof HelloMessage) {
                client.send(new PlayerConnectMessage(playerName));

                Log.fine("Got Hello, Sending player connect");
            } else if (message instanceof PlayerConnectMessage) {
                PlayerConnectMessage pcm = (PlayerConnectMessage) message;
                playerManager.addPlayer(pcm.getPlayerName());

                Log.info(pcm.getPlayerName(), "connected");
            } else if (message instanceof PlayerDisconnectMessage) {
                PlayerDisconnectMessage pdm = (PlayerDisconnectMessage) message;
                if (pdm.getPlayerName().isEmpty()) {
                    Gdx.app.exit();
                }

                playerManager.removePlayer(pdm.getPlayerName());
                Log.info(pdm.getPlayerName(), "disconnected");
            } else if (message instanceof PlayerUpdateMessage) {
                PlayerUpdateMessage pum = (PlayerUpdateMessage) message;

                Player p = playerManager.getPlayer(pum.getPlayer());
                if (p == null) {
                    Log.warn("Player Not Found:", pum.getPlayer());
                } else {
                    p.getPosition().set(pum.getPos());
                    p.getVelocity().set(pum.getVel());

                    if (!pum.isAlive()) {
                        p.kill();
                    }
                }
            } else if (message instanceof NewShotMessage) {
                NewShotMessage nsm = (NewShotMessage) message;

                shotManager.addShot(nsm.getShot(playerManager));
            }
        }
    }

    private class AddShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            if (shot.player == playerManager.getLocalPlayer()) {
                client.send(new NewShotMessage(shot));
            }
        }

        @Override
        public void shotTerrainCollision(Shot shot) {
        }

        @Override
        public void shotPlayerCollision(Shot shot, Player player) {
        }
    }
}
