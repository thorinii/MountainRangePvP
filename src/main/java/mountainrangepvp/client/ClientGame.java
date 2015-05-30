package mountainrangepvp.client;

import com.badlogic.gdx.Gdx;
import mountainrangepvp.core.ChatManager;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.Log;
import mountainrangepvp.client.input.InputHandler;
import mountainrangepvp.client.renderer.GameScreen;
import mountainrangepvp.core.*;
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
    private ClientId localId;
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


        eventBus.subscribe(SnapshotEvent.class, new EventHandler<SnapshotEvent>() {
            @Override
            public void receive(SnapshotEvent event) {
                handleSnapshot(event.snapshot());
            }
        });
        eventBus.subscribe(ConnectedEvent.class, new EventHandler<ConnectedEvent>() {
            @Override
            public void receive(ConnectedEvent event) {
                localId = event.localId();
            }
        });
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

        if (session != null) {
            inputHandler.update(config.TIMESTEP, session.getCameraRelativeToPlayer());
            session.update(dt);
        }

        timeSinceLastUpdate = 0;

        if (gameScreen != null) gameScreen.render(dt, client.lag());

        eventBus.resetMessagesPerFrame();
    }

    private void handleSnapshot(Snapshot s) {
        if (session == null) {
            ChatManager chatManager = new ChatManager();

            session = new Session(log, eventBus, localId, s, chatManager);

            gameScreen = new GameScreen(log, eventBus, session);
        }
    }
}
