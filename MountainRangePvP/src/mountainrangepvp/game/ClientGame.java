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
import mountainrangepvp.terrain.HeightMap;
import mountainrangepvp.terrain.HillsHeightMap;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.GameClient;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.mp.message.Proxy;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotListener;
import mountainrangepvp.shot.ShotManager;
import mountainrangepvp.terrain.Terrain;
import mountainrangepvp.util.Timer;

/**
 *
 * @author lachlan
 */
public class ClientGame extends Game {

    private final String playerName;
    private final String serverIP;
    private GameClient client;
    //
    private GameWorld world;
    private PlayerManager playerManager;
    private ShotManager shotManager;
    private PhysicsSystem physicsSystem;
    private InputHandler inputHandler;
    private AudioManager audioManager;
    //
    private GameScreen gameScreen;
    //
    private final Timer playerUpdateTimer;

    public ClientGame(String playerName, String serverIP) {
        this.playerName = playerName;
        this.serverIP = serverIP;

        this.world = new GameWorld(null, playerManager, shotManager);
        this.playerUpdateTimer = new Timer();
    }

    @Override
    public void create() {
        try {
            Log.info("Connecting to", serverIP);

            client = new GameClient(world, serverIP);
            client.addMessageListener(new ServerMessageListener());
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

            playerUpdateTimer.update();
            if (playerUpdateTimer.getTime() > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
                // TODO: send update
                playerUpdateTimer.reset();
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
        public void accept(Message message, int id) throws IOException {
            if (message instanceof KillConnectionMessage) {
                KillConnectionMessage kill = (KillConnectionMessage) message;

                if (kill.getReason() != KillConnectionMessage.Reason.ServerShutdown)
                    JOptionPane.showMessageDialog(null, "Error: " + kill.
                            getReason(),
                                                  "Mountain Range PvP",
                                                  JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "Server quit",
                                                  "Mountain Range PvP",
                                                  JOptionPane.ERROR_MESSAGE);
                Gdx.app.exit();
            } else if (message instanceof NewWorldMessage) {
                NewWorldMessage newWorldMessage = (NewWorldMessage) message;

                Log.info("Received Seed", newWorldMessage.getSeed(),
                         "Changing Map");

                HeightMap heightMap;
                switch (newWorldMessage.getWorldType()) {
                    case Hills:
                        heightMap = new HillsHeightMap(newWorldMessage.getSeed());
                        break;
                    default:
                        heightMap = null;
                }

                world.setTerrain(new Terrain(heightMap));

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
            }
        }
    }
//    private class ServerMessageListener implements MessageListener {
//
//        @Override
//        public void accept(Message message, Proxy proxy) throws IOException {
//            if (message instanceof KillConnectionMessage) {
//                KillConnectionMessage kill = (KillConnectionMessage) message;
//
//                // TODO: some reasons might be good
//
//                JOptionPane.showMessageDialog(null, "Error in server " + kill.
//                        getReason(),
//                                              "Mountain Range PvP",
//                                              JOptionPane.ERROR_MESSAGE);
//            } else if (message instanceof NewWorldMessage) {
//                NewWorldMessage seedMessage = (NewWorldMessage) message;
//
//                Log.info("Received Seed", seedMessage.getSeed(), "Changing Map");
//
//                heightMap = new HillsHeightMap(seedMessage.getSeed());
//                playerManager = new PlayerManager(playerName);
//                shotManager = new ShotManager(heightMap, playerManager);
//                physicsSystem = new PhysicsSystem(heightMap, playerManager);
//
//                shotManager.addShotListener(new AddShotListener());
//
//                inputHandler = new InputHandler(playerManager, shotManager);
//                inputHandler.register();
//
//                audioManager = new AudioManager(playerManager, shotManager);
//                audioManager.loadAudio();
//
//                gameScreen = new GameScreen(heightMap, playerManager,
//                                            shotManager);
//                setScreen(gameScreen);
//            } else if (message instanceof ClientHelloMessage) {
//                client.send(new PlayerConnectMessage(playerName));
//
//                Log.fine("Got Hello, Sending player connect");
//            } else if (message instanceof PlayerConnectMessage) {
//                PlayerConnectMessage pcm = (PlayerConnectMessage) message;
//                playerManager.addPlayer(pcm.getPlayerName());
//
//                Log.info(pcm.getPlayerName(), "connected");
//            } else if (message instanceof PlayerDisconnectMessage) {
//                PlayerDisconnectMessage pdm = (PlayerDisconnectMessage) message;
//                if (pdm.getPlayerName().isEmpty()) {
//                    Gdx.app.exit();
//                }
//
//                playerManager.removePlayer(pdm.getPlayerName());
//                Log.info(pdm.getPlayerName(), "disconnected");
//            } else if (message instanceof PlayerUpdateMessage) {
//                PlayerUpdateMessage pum = (PlayerUpdateMessage) message;
//
//                Player p = playerManager.getPlayer(pum.getPlayer());
//                if (p == null) {
//                    Log.warn("Player Not Found:", pum.getPlayer());
//                } else {
//                    p.getPosition().set(pum.getPos());
//                    p.getVelocity().set(pum.getVel());
//                    p.getGunDirection().set(pum.getGun());
//
//                    p.setAlive(pum.isAlive());
//                }
//            } else if (message instanceof NewShotMessage) {
//                NewShotMessage nsm = (NewShotMessage) message;
//
//                shotManager.addShot(nsm.getShot(playerManager));
//            }
//        }
//    }

    private class AddShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            if (shot.player == playerManager.getLocalPlayer()) {
                // TODO: send shot message
//                client.send(new NewShotMessage(shot));
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
