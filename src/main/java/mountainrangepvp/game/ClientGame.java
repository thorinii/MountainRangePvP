package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import mountainrangepvp.GameConfig;
import mountainrangepvp.Log;
import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.chat.ChatManager;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.GameClient;
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

import javax.swing.*;
import java.io.IOException;

/**
 * @author lachlan
 */
public class ClientGame extends Game {

    private final String serverIP;
    private final GameClient client;
    private final GameConfig config;

    private GameWorld world;
    private PhysicsSystem physicsSystem;
    private InputHandler inputHandler;
    private AudioManager audioManager;

    private GameScreen gameScreen;

    private final Timer limitFPSTimer;

    public ClientGame(GameConfig config) {
        this.serverIP = config.serverIP;
        this.config = config;

        world = new GameWorld();
        world.setTeamModeOn(config.teamModeOn);

        PlayerManager playerManager = new ClientPlayerManager(config.playerName,
                                                              config.team);
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ClientShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        client = new GameClient(world, serverIP);
        client.addMessageListener(new ServerMessageListener());

        limitFPSTimer = new Timer();
    }

    @Override
    public void create() {
        try {
            Log.info("Connecting to", serverIP);

            client.start();
        } catch (IOException ioe) {
            Log.warn("Error starting server connection:", ioe);
            JOptionPane.showMessageDialog(null, "Error starting client " + ioe,
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }
    }

    private float sinceLastUpdate = 0;

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        sinceLastUpdate += dt;

        if (sinceLastUpdate > config.TIMESTEP) {
            client.update();
            if (gameScreen != null) {
                inputHandler.update(config.TIMESTEP);
                world.update(config.TIMESTEP);
                physicsSystem.update(config.TIMESTEP);
            }

            sinceLastUpdate = 0;


            if (gameScreen != null) {
                gameScreen.render(dt);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        client.stop();
    }

    private class ServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof KillConnectionMessage) {
                final KillConnectionMessage kill = (KillConnectionMessage) message;

                Gdx.app.exit();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (kill.getReason() != KillConnectionMessage.Reason.ServerShutdown)
                            JOptionPane.showMessageDialog(null,
                                                          "Error: " + kill.getReason(),
                                                          "Mountain Range PvP",
                                                          JOptionPane.ERROR_MESSAGE);
                        else
                            JOptionPane.showMessageDialog(null, "Server quit",
                                                          "Mountain Range PvP",
                                                          JOptionPane.ERROR_MESSAGE);
                    }
                });
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
                world.setTeamModeOn(newWorldMessage.isTeamModeOn());

                physicsSystem = new PhysicsSystem(world);

                inputHandler = new InputHandler(world);
                inputHandler.register();

                audioManager = new AudioManager(world.getPlayerManager(),
                                                world.getShotManager(),
                                                config);
                audioManager.loadAudio();

                gameScreen = new GameScreen(world);
                setScreen(gameScreen);
            }
        }
    }
}
