package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.GameClient;
import mountainrangepvp.mp.message.KillConnectionMessage;
import mountainrangepvp.mp.message.Message;
import mountainrangepvp.mp.message.MessageListener;
import mountainrangepvp.mp.message.NewWorldMessage;
import mountainrangepvp.renderer.GameScreen;
import mountainrangepvp.util.Log;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.physics.PhysicsSystem;
import mountainrangepvp.world.player.ClientPlayerManager;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ClientShotManager;
import mountainrangepvp.world.terrain.HeightMap;
import mountainrangepvp.world.terrain.HillsHeightMap;
import mountainrangepvp.world.terrain.Terrain;

import java.io.IOException;

/**
 * The state that lasts between instances.
 */
public abstract class Game {

    public final GameConfig config;

    public final GameClient client;
    public final PhysicsSystem physicsSystem;
    public final InputHandler inputHandler;
    public final AudioManager audioManager;
    public final GameScreen gameScreen;

    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    protected GameWorld world;
    private ClientShotManager shotManager;

    public Game(GameConfig config) {
        this.config = config;

        physicsSystem = new PhysicsSystem();

        playerManager = new ClientPlayerManager(config.playerName, config.team);
        chatManager = new ChatManager(playerManager);

        inputHandler = new InputHandler(chatManager);
        inputHandler.register();

        audioManager = new AudioManager(playerManager, config);
        audioManager.loadAudio();

        client = new GameClient(world, config.serverIP);
        client.addMessageListener(new MapChangeListener());

        gameScreen = new GameScreen();
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
            if (world != null) {
                inputHandler.update(world, config.TIMESTEP);
                world.update(config.TIMESTEP);
                physicsSystem.update(world, config.TIMESTEP);
            }

            timeSinceLastUpdate = 0;


            if (world != null) {
                gameScreen.render(dt);
            }
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

                shotManager = new ClientShotManager();
                world = new GameWorld(playerManager, shotManager, chatManager, new Terrain(heightMap), config.teamModeOn);
                shotManager.setWorld(world);
                gameScreen.setWorld(world);

                audioManager.listenTo(shotManager);
                inputHandler.setShotManager(shotManager);
            }
        }
    }
}
