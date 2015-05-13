package mountainrangepvp.game.world;

import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.Log;

/**
 * State that doesn't change between maps.
 */
public class Session {
    private final Log log;
    private final EventBus eventBus;
    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    private Snapshot snapshot;
    private Map map;

    public Session(Log log, EventBus eventBus, PlayerManager playerManager, ChatManager chatManager) {
        this.log = log;
        this.eventBus = eventBus;
        this.playerManager = playerManager;
        this.chatManager = chatManager;

        this.map = null;

        subscribeTo(eventBus);
    }

    private void subscribeTo(EventBus eventBus) {
        eventBus.subscribe(SnapshotEvent.class, new EventHandler<SnapshotEvent>() {
            @Override
            public void receive(SnapshotEvent event) {
                log.info("New snapshot " + event.snapshot());
                snapshot = event.snapshot();
            }
        });
    }

    public Map getMap() {
        if (map == null)
            throw new IllegalStateException("No map loaded");
        return map;
    }

    public boolean hasMap() {
        return map != null;
    }

    public void update(float dt) {
        if (hasMap())
            map.update(dt);
    }
}
