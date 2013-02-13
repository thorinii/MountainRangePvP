/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.io.IOException;
import javax.swing.JOptionPane;
import mountainrangepvp.Log;
import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.mp.Proxy;
import mountainrangepvp.mp.Server;
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
public class ServerGame extends Game {

    private final HeightMap heightMap;
    //
    private final String playerName;
    private final Server server;
    //
    private final PlayerManager playerManager;
    private final ShotManager shotManager;
    private final PhysicsSystem physicsSystem;
    private final InputHandler inputHandler;
    private final AudioManager audioManager;
    //
    private GameScreen gameScreen;
    //
    private int playerUpdateTimer;

    public ServerGame(String playerName, int seed) {
        this.playerName = playerName;

        heightMap = new MountainHeightMap(seed);
        server = new Server(seed);

        playerManager = new PlayerManager(playerName);
        shotManager = new ShotManager(heightMap, playerManager);
        physicsSystem = new PhysicsSystem(heightMap, playerManager);
        inputHandler = new InputHandler(playerManager, shotManager);
        audioManager = new AudioManager(playerManager, shotManager);

        shotManager.addShotListener(new AddShotListener());
    }

    @Override
    public void create() {
        try {
            Log.info("Starting Server...");

            server.getMessageQueue().addListener(new ClientMessageListener());
            server.start();
        } catch (IOException ioe) {
            Log.warn("Error starting server:", ioe);
            JOptionPane.showMessageDialog(null, "Error starting server",
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }

        inputHandler.register();
        audioManager.loadAudio();

        gameScreen = new GameScreen(heightMap, playerManager, shotManager);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        server.update();

        inputHandler.update(dt);
        shotManager.update(dt);
        physicsSystem.update(dt);
        gameScreen.render(dt);

        playerUpdateTimer += (int) (1000 * dt);
        if (playerUpdateTimer > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
            server.broadcast(new PlayerUpdateMessage(playerManager.
                    getLocalPlayer()));
            playerUpdateTimer = 0;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        server.stop();
    }

    private class ClientMessageListener implements MessageListener {

        @Override
        public void accept(Message message, Proxy proxy) throws IOException {
            if (message instanceof HelloMessage) {
                server.send(new PlayerConnectMessage(playerName), proxy);

                Log.fine("Got Hello, Sending player connect");
            } else if (message instanceof PlayerConnectMessage) {
                PlayerConnectMessage pcm = (PlayerConnectMessage) message;
                playerManager.addPlayer(pcm.getPlayerName());

                Log.info(pcm.getPlayerName(), "connected");

                server.broadcastExcept(pcm, proxy);

                for (Player p : playerManager.getPlayers()) {
                    if (!p.getName().equals(pcm.getPlayerName()) && !p.getName().
                            equals(playerName)) {
                        server.send(new PlayerConnectMessage(p.getName()), proxy);
                        Log.info("sending", pcm.getPlayerName(), p.getName());
                    }
                }

            } else if (message instanceof PlayerDisconnectMessage) {
                String name = ((Server.ClientProxy) proxy).getPlayerName();

                playerManager.removePlayer(name);
                server.broadcastExcept(new PlayerDisconnectMessage(name), proxy);

                Log.info(name, "disconnected");
            } else if (message instanceof PlayerUpdateMessage) {
                PlayerUpdateMessage pum = (PlayerUpdateMessage) message;

                Player p = playerManager.getPlayer(pum.getPlayer());
                p.getPosition().set(pum.getPos());
                p.getVelocity().set(pum.getVel());
                p.getGunDirection().set(pum.getGun());
                p.setAlive(pum.isAlive());

                server.broadcastExcept(message, proxy);
            } else if (message instanceof NewShotMessage) {
                NewShotMessage nsm = (NewShotMessage) message;

                shotManager.addShot(nsm.getShot(playerManager));
                server.broadcastExcept(message, proxy);
            }
        }
    }

    private class AddShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            if (shot.player == playerManager.getLocalPlayer()) {
                server.broadcast(new NewShotMessage(shot));
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
