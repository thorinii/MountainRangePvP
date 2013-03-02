/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import javax.swing.JOptionPane;
import mountainrangepvp.GameConfig;
import mountainrangepvp.Log;
import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.chat.ChatManager;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.GameClient;
import mountainrangepvp.mp.GameServer;
import mountainrangepvp.mp.message.KillConnectionMessage;
import mountainrangepvp.mp.message.Message;
import mountainrangepvp.mp.message.MessageListener;
import mountainrangepvp.mp.message.NewWorldMessage;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.ClientPlayerManager;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.ClientShotManager;
import mountainrangepvp.shot.ShotManager;
import mountainrangepvp.terrain.HeightMap;
import mountainrangepvp.terrain.HillsHeightMap;
import mountainrangepvp.terrain.Terrain;
import mountainrangepvp.util.Timer;

/**
 *
 * @author lachlan
 */
public class ServerGame extends Game {

    private final String playerName;
    private final int seed;
    //
    private final GameClient client;
    private GameServer server;
    //
    private GameWorld world;
    private PhysicsSystem physicsSystem;
    private InputHandler inputHandler;
    private AudioManager audioManager;
    //
    private GameScreen gameScreen;
    //
    private final Timer limitFPSTimer;

    public ServerGame(GameConfig config) {
        this.playerName = config.playerName;
        this.seed = config.seed;

        world = new GameWorld();

        PlayerManager playerManager = new ClientPlayerManager(config.playerName,
                                                              config.team);
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ClientShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        client = new GameClient(world, "localhost");
        client.addMessageListener(new ServerMessageListener());

        limitFPSTimer = new Timer();
    }

    @Override
    public void create() {
        try {
            server = GameServer.startBasicServer(seed);
            client.start();
        } catch (IOException ioe) {
            Log.warn("Error starting server connection:", ioe);
            JOptionPane.showMessageDialog(null, "Error starting server " + ioe,
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        limitFPSTimer.update();

        if (limitFPSTimer.getTime() < (1000 / 60)) {
            return;
        } else
            limitFPSTimer.reset();

        client.update();

        if (gameScreen != null) {
            inputHandler.update(dt);
            world.update(dt);
            physicsSystem.update(dt);
            gameScreen.render(dt);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        client.stop();
        server.stop();
    }

    private class ServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof KillConnectionMessage) {
                KillConnectionMessage kill = (KillConnectionMessage) message;

                Gdx.app.exit();

                if (kill.getReason() != KillConnectionMessage.Reason.ServerShutdown)
                    JOptionPane.showMessageDialog(null, "Error: " + kill.
                            getReason(),
                                                  "Mountain Range PvP",
                                                  JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "Server quit",
                                                  "Mountain Range PvP",
                                                  JOptionPane.ERROR_MESSAGE);
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

                physicsSystem = new PhysicsSystem(world);

                inputHandler = new InputHandler(world);
                inputHandler.register();

                audioManager = new AudioManager(world.getPlayerManager(), world.
                        getShotManager());
                audioManager.loadAudio();

                gameScreen = new GameScreen(world);
                setScreen(gameScreen);
            }
        }
    }
}
