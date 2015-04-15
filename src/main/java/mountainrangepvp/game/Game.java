package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.game.audio.AudioManager;
import mountainrangepvp.game.input.InputHandler;
import mountainrangepvp.game.mp.GameClient;
import mountainrangepvp.game.mp.message.KillConnectionMessage;
import mountainrangepvp.game.mp.message.Message;
import mountainrangepvp.game.mp.message.MessageListener;
import mountainrangepvp.game.mp.message.NewWorldMessage;
import mountainrangepvp.game.renderer.GameScreen;
import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.game.world.*;
import mountainrangepvp.util.Log;

import java.io.IOException;

/**
 * Container of game systems.
 */
public abstract class Game {

    public final GameSettings config;

    public final GameClient client;
    public final PhysicsSystem physicsSystem;
    public final InputHandler inputHandler;
    public final AudioManager audioManager;
    public final GameScreen gameScreen;

    public final Instance instance;

    public Game(GameSettings config) {
        this.config = config;

        physicsSystem = new PhysicsSystem();

        PlayerManager playerManager = new ClientPlayerManager(config.playerName, config.team);
        ChatManager chatManager = new ChatManager(playerManager);

        instance = new Instance(playerManager, chatManager);

        inputHandler = new InputHandler(chatManager);
        inputHandler.register();

        audioManager = new AudioManager(playerManager, config);
        audioManager.loadAudio();

        client = new GameClient(instance, config.serverIP);
        client.addMessageListener(new MapChangeListener());

        gameScreen = new GameScreen(instance);
    }

    public void start() {
        try {
            client.start();
        } catch (IOException ioe) {
            Log.crash("Error connecting to server", ioe);
        }
    }

    public void kill() {
        client.stop();
    }


    private float timeSinceLastUpdate = 0;

    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        timeSinceLastUpdate += dt;

        if (timeSinceLastUpdate > config.TIMESTEP) {
            client.update();
            if (instance.hasMap()) {
                inputHandler.update(instance, config.TIMESTEP);
                instance.update(config.TIMESTEP);
                physicsSystem.update(instance, config.TIMESTEP);
            }

            timeSinceLastUpdate = 0;

            gameScreen.render(dt);
        }
    }

    private class MapChangeListener implements MessageListener {
        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof KillConnectionMessage) {
                KillConnectionMessage kill = (KillConnectionMessage) message;

                Log.crash("Server disconnected: " + kill.getReason());
            } else if (message instanceof NewWorldMessage) {
                NewWorldMessage newWorldMessage = (NewWorldMessage) message;

                Log.info("Received Seed", newWorldMessage.getSeed(), "Changing Map");

                HeightMap heightMap;
                switch (newWorldMessage.getWorldType()) {
                    case Hills:
                        heightMap = new HillsHeightMap(newWorldMessage.getSeed());
                        break;
                    default:
                        heightMap = null;
                }

                Terrain terrain = new Terrain(heightMap);
                ShotManager shotManager = new ShotManager(instance.playerManager, terrain, false, newWorldMessage.isTeamModeOn());
                Map map = new Map(shotManager, terrain, newWorldMessage.isTeamModeOn());

                instance.setMap(map);

                audioManager.listenTo(shotManager);
                inputHandler.setShotManager(shotManager);
            }
        }
    }
}
