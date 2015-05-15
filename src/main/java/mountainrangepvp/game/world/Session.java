package mountainrangepvp.game.world;

import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.Log;

/**
 * State that doesn't change between maps.
 */
public class Session {
    private final Log log;
    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    private Snapshot snapshot;
    private Terrain terrain;

    public Session(Log log, EventBus eventBus, PlayerManager playerManager, ChatManager chatManager) {
        this.log = log;
        this.playerManager = playerManager;
        this.chatManager = chatManager;

        this.snapshot = null;

        subscribeTo(eventBus);
    }

    private void subscribeTo(EventBus eventBus) {
        eventBus.subscribe(SnapshotEvent.class, new EventHandler<SnapshotEvent>() {
            @Override
            public void receive(SnapshotEvent event) {
                snapshot = event.snapshot();

                if(terrain == null || terrain.getSeed() != snapshot.seed()) {
                    HeightMap heightMap = new HillsHeightMap(snapshot.seed());
                    terrain = new Terrain(heightMap);
                }
            }
        });
    }

    public Snapshot getSnapshot() {
        if (snapshot == null)
            throw new IllegalStateException("No snapshot available");
        return snapshot;
    }

    public Terrain getTerrain() {
        if(terrain == null)
            throw new IllegalStateException("No terrain available");
        return terrain;
    }

    public boolean hasSnapshot() {
        return snapshot != null;
    }
}
