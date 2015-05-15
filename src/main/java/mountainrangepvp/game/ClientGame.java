package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.game.input.InputHandler;
import mountainrangepvp.game.renderer.GameScreen;
import mountainrangepvp.game.world.*;
import mountainrangepvp.net.ServerInterface;
import mountainrangepvp.net.client.Client;

/**
 * Container of game systems.
 */
public class ClientGame {

    private final GameSettings config;
    private final Log log;

    private final EventBus eventBus;
    private final Client client;
    private final AudioManager audioManager;
    private final InputHandler inputHandler;

    private Session session;
    private GameScreen gameScreen;

    public ClientGame(GameSettings config, ServerInterface server) {
        this.config = config;
        log = new Log("client");

        eventBus = new EventBus();

        client = Client.newClient(log, eventBus, server, config.nickname);

        audioManager = new AudioManager();
        audioManager.loadAudio(Sounds.SOUNDS);
        audioManager.setMuted(true);

        inputHandler = new InputHandler(eventBus,
                                        Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        inputHandler.register();

        eventBus.subscribe(NewSessionEvent.class, new NewSessionHandler());
    }

    public void start() {
        try {
            client.start();
        } catch (InterruptedException e) {
            log.crash("Could not connect to server", e);
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
        eventBus.flushPendingMessages();

        if (session != null && session.hasSnapshot()) {
            inputHandler.update(config.TIMESTEP);
            session.update(dt);
        }

        timeSinceLastUpdate = 0;

        if (gameScreen != null) gameScreen.render(dt, client.lag());

        eventBus.resetMessagesPerFrame();
    }

    private class NewSessionHandler implements EventHandler<NewSessionEvent> {
        @Override
        public void receive(NewSessionEvent event) {
            log.info("SessionInfo: teamsOn " + event.teamsOn());

            PlayerManager playerManager = new PlayerManager(config.nickname, config.team);
            ChatManager chatManager = new ChatManager(playerManager);

            session = new Session(log, eventBus, event.localId(), playerManager, chatManager);

            gameScreen = new GameScreen(log, eventBus, session);
        }
    }
}
