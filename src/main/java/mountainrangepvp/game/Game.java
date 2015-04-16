package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.game.input.InputHandler;
import mountainrangepvp.game.mp.GameClient;
import mountainrangepvp.game.mp.message.KillConnectionMessage;
import mountainrangepvp.game.mp.message.Message;
import mountainrangepvp.game.mp.message.MessageListener;
import mountainrangepvp.game.mp.message.NewWorldMessage;
import mountainrangepvp.game.renderer.GameScreen;
import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.game.world.*;
import mountainrangepvp.net.Client;
import mountainrangepvp.net.ServerInterface;

import java.io.IOException;

/**
 * Container of game systems.
 */
public abstract class Game {

    public final GameSettings config;

    public final EventBus eventbus;
    public final Client client;
    public final GameClient client_OLD;
    public final PhysicsSystem physicsSystem;
    public final InputHandler inputHandler;
    public final AudioManager audioManager;
    public final GameScreen gameScreen;

    public final Instance instance;

    public Game(GameSettings config, ServerInterface server) {
        this.config = config;

        eventbus = new EventBus();

        client = Client.newClient(eventbus, server);

        physicsSystem = new PhysicsSystem();

        PlayerManager playerManager = new ClientPlayerManager(config.playerName, config.team);
        ChatManager chatManager = new ChatManager(playerManager);

        instance = new Instance(playerManager, chatManager);

        inputHandler = new InputHandler(eventbus, chatManager);
        inputHandler.register();

        audioManager = new AudioManager();
        audioManager.loadAudio(Sounds.SOUNDS);
        audioManager.setMuted(true);

        client_OLD = new GameClient(instance, config.serverIP);
        client_OLD.addMessageListener(new MapChangeListener());

        gameScreen = new GameScreen(eventbus, instance);
    }

    public void start() {
        try {
            client_OLD.start();
        } catch (IOException ioe) {
            Log.crash("Error connecting to server", ioe);
        }
    }

    public void kill() {
        client_OLD.stop();
    }


    private float timeSinceLastUpdate = 0;

    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        timeSinceLastUpdate += dt;

        if (timeSinceLastUpdate > config.TIMESTEP)
            update(dt);
    }

    private void update(float dt) {
        eventbus.flush();

        client_OLD.update();
        if (instance.hasMap()) {
            inputHandler.update(instance, config.TIMESTEP);
            instance.update(config.TIMESTEP);
            physicsSystem.update(instance, config.TIMESTEP);
        }

        timeSinceLastUpdate = 0;

        gameScreen.render(dt);
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

                shotManager.addShotListener(new AudioShotListener(instance.playerManager, audioManager));
                inputHandler.setShotManager(shotManager);
            }
        }
    }
}
