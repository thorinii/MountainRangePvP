package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.LegacyLog;
import mountainrangepvp.game.world.NewSessionEvent;
import mountainrangepvp.game.input.InputHandler;
import mountainrangepvp.game.renderer.GameScreen;
import mountainrangepvp.game.world.*;
import mountainrangepvp.net.ServerInterface;
import mountainrangepvp.net.client.Client;

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

    public Game(GameSettings config, ServerInterface server) {
        this.config = config;

        eventbus = new EventBus(Thread.currentThread());

        client = Client.newClient(eventbus, server, config.nickname);

        physicsSystem = new PhysicsSystem();

        audioManager = new AudioManager();
        audioManager.loadAudio(Sounds.SOUNDS);
        audioManager.setMuted(true);

        eventbus.subscribe(NewSessionEvent.class, new NewSessionHandler());
    }

    public void start() {
        try {
            client.start();
        } catch (InterruptedException e) {
            LegacyLog.crash("Could not connect to server", e);
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

    private class NewSessionHandler implements EventHandler<NewSessionEvent> {
        @Override
        public void receive(NewSessionEvent event) {
            LegacyLog.info("SessionInfo: teamsOn " + event.teamsOn());

            PlayerManager playerManager = new ClientPlayerManager(config.nickname, config.team);
            ChatManager chatManager = new ChatManager(playerManager);

            session = new Session(eventbus, event.teamsOn(), playerManager, chatManager);

            inputHandler = new InputHandler(eventbus, null);// chatManager);
            inputHandler.register();

            gameScreen = new GameScreen(eventbus, session);
        }
    }
}
