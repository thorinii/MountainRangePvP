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
 * All the important bits of the game.
 */
public abstract class Game implements ApplicationListener {

    protected final GameConfig config;

    private final GameClient client;

    protected final GameWorld world;
    protected PhysicsSystem physicsSystem;
    protected InputHandler inputHandler;
    protected AudioManager audioManager;

    protected GameScreen gameScreen;

    public Game(GameConfig config) {
        this.config = config;

        world = new GameWorld();
        world.setTeamModeOn(config.teamModeOn);

        PlayerManager playerManager = new ClientPlayerManager(config.playerName, config.team);
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ClientShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        client = new GameClient(world, config.serverIP);
        client.addMessageListener(new MapChangeListener());
    }

    @Override
    public void create() {
        try {
            client.start();
        } catch (IOException ioe) {
            Log.crash("Error connecting to server", ioe);
        }
    }

    @Override
    public void dispose() {
        client.stop();
    }


    private float timeSinceLastUpdate = 0;

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        timeSinceLastUpdate += dt;

        if (timeSinceLastUpdate > config.TIMESTEP) {
            client.update();
            if (gameScreen != null) {
                inputHandler.update(config.TIMESTEP);
                world.update(config.TIMESTEP);
                physicsSystem.update(config.TIMESTEP);
            }

            timeSinceLastUpdate = 0;


            if (gameScreen != null) {
                gameScreen.render(dt);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
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

                physicsSystem = new PhysicsSystem(world);

                inputHandler = new InputHandler(world);
                inputHandler.register();

                audioManager = new AudioManager(world.getPlayerManager(),
                                                world.getShotManager(),
                                                config);
                audioManager.loadAudio();

                gameScreen = new GameScreen(world);
            }
        }
    }
}
