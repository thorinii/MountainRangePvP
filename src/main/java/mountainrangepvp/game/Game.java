package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.game.event.NewSessionEvent;
import mountainrangepvp.game.input.InputHandler;
import mountainrangepvp.game.mp.message.KillConnectionMessage;
import mountainrangepvp.game.mp.message.Message;
import mountainrangepvp.game.mp.message.MessageListener;
import mountainrangepvp.game.mp.message.NewWorldMessage;
import mountainrangepvp.game.renderer.GameScreen;
import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.game.world.*;
import mountainrangepvp.net.ServerInterface;
import mountainrangepvp.net.client.Client;

import java.io.IOException;

/**
 * Container of game systems.
 */
public class Game {

    private final GameSettings config;

    private final EventBus eventbus;
    private final Client client;
    private final PhysicsSystem physicsSystem;
    private final AudioManager audioManager;

    private InputHandler inputHandler;
    private Session session;
    private GameScreen gameScreen;

    public Game(final GameSettings config, ServerInterface server) {
        this.config = config;

        eventbus = new EventBus(Thread.currentThread());

        client = Client.newClient(eventbus, server, config.nickname);

        physicsSystem = new PhysicsSystem();

        audioManager = new AudioManager();
        audioManager.loadAudio(Sounds.SOUNDS);
        audioManager.setMuted(true);

        eventbus.subscribe(NewSessionEvent.class, new EventHandler<NewSessionEvent>() {
            @Override
            public void receive(NewSessionEvent event) {
                Log.info("SessionInfo: teamsOn " + event.teamsOn());

                PlayerManager playerManager = new ClientPlayerManager(config.nickname, config.team);
                ChatManager chatManager = new ChatManager(playerManager);

                session = new Session(playerManager, chatManager);

                inputHandler = new InputHandler(eventbus, null);// chatManager);
                inputHandler.register();

                gameScreen = new GameScreen(eventbus, session);
            }
        });
    }

    public void start() {
        try {
            client.start();
        } catch (InterruptedException e) {
            Log.crash("Could not connect to server", e);
            return;
        }

        // TODO: go to loading screen here (and wait for connection)
    }


    public void kill() {
        client.shutdown();
    }


    private float timeSinceLastUpdate = 0;

    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        timeSinceLastUpdate += dt;

        if (timeSinceLastUpdate > config.TIMESTEP)
            update(dt);
    }

    private void update(float dt) {
        eventbus.flushPendingMessages();
        // TODO: client.update();

        if (session != null && session.hasMap()) {
            inputHandler.update(session, config.TIMESTEP);
            session.update(config.TIMESTEP);
            physicsSystem.update(session, config.TIMESTEP);
        }

        timeSinceLastUpdate = 0;

        if (gameScreen != null) gameScreen.render(dt);

        eventbus.resetMessagesPerFrame();
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
                ShotManager shotManager = new ShotManager(session.playerManager, terrain, false, newWorldMessage.isTeamModeOn());
                Map map = new Map(shotManager, terrain, newWorldMessage.isTeamModeOn());

                session.setMap(map);

                shotManager.addShotListener(new AudioShotListener(session.playerManager, audioManager));
                inputHandler.setShotManager(shotManager);
            }
        }
    }
}
