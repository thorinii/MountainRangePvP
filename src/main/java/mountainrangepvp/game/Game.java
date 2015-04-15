package mountainrangepvp.game;

import com.badlogic.gdx.ApplicationListener;
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
import mountainrangepvp.world.shot.ShotManager;
import mountainrangepvp.world.terrain.HeightMap;
import mountainrangepvp.world.terrain.HillsHeightMap;
import mountainrangepvp.world.terrain.Terrain;

import java.io.IOException;

/**
 * The state that lasts between instances.
 */
public abstract class Game {

    private final GameConfig config;

    private final GameClient client;
    private final PhysicsSystem physicsSystem;
    private final InputHandler inputHandler;
    private final AudioManager audioManager;
    private final GameScreen gameScreen;

    protected final GameWorld world;

    private boolean hasMap;

    public Game(GameConfig config) {
        this.config = config;

        world = new GameWorld();
        world.setTeamModeOn(config.teamModeOn);

        physicsSystem = new PhysicsSystem();

        PlayerManager playerManager = new ClientPlayerManager(config.playerName, config.team);
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ClientShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        inputHandler = new InputHandler(chatManager, shotManager);
        inputHandler.register();

        audioManager = new AudioManager(playerManager, shotManager, config);
        audioManager.loadAudio();

        client = new GameClient(world, config.serverIP);
        client.addMessageListener(new MapChangeListener());

        gameScreen = new GameScreen();

        hasMap = false;
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
            if (hasMap) {
                inputHandler.update(world, config.TIMESTEP);
                world.update(config.TIMESTEP);
                physicsSystem.update(world, config.TIMESTEP);
            }

            timeSinceLastUpdate = 0;


            if (hasMap) {
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

                world.setTerrain(new Terrain(heightMap));
                world.setTeamModeOn(newWorldMessage.isTeamModeOn());

                gameScreen.setWorld(world);

                hasMap = true;
            }
        }
    }
}
