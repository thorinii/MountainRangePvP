package mountainrangepvp.game.world;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.Log;

/**
 * State that doesn't change between maps.
 */
public class Session {
    private final Log log;
    private final ClientId localId;
    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    private Snapshot snapshot;
    private Terrain terrain;
    private Camera camera;

    public Session(Log log, EventBus eventBus, ClientId localId, PlayerManager playerManager, ChatManager chatManager) {
        this.log = log;
        this.localId = localId;
        this.playerManager = playerManager;
        this.chatManager = chatManager;

        this.snapshot = null;
        this.terrain = null;
        this.camera = new Camera(new Vector2(0,0));

        subscribeTo(eventBus);
    }

    private void subscribeTo(EventBus eventBus) {
        eventBus.subscribe(SnapshotEvent.class, new EventHandler<SnapshotEvent>() {
            @Override
            public void receive(SnapshotEvent event) {
                snapshot = event.snapshot();

                if (terrain == null || terrain.getSeed() != snapshot.seed()) {
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

    public boolean hasSnapshot() {
        return snapshot != null;
    }

    public Terrain getTerrain() {
        if (terrain == null)
            throw new IllegalStateException("No terrain available");
        return terrain;
    }

    public Vector2 getCameraCentre() {
        return camera.centre();
    }

    private PlayerEntity localPlayer() {
        if (snapshot.hasPlayerEntity(localId))
            return snapshot.getPlayerEntity(localId);
        else
            return null;
    }

    public void update(float dt) {
        PlayerEntity p = localPlayer();
        if (p != null)
            this.camera = camera.centreOn(snapshot.getPlayerEntity(localId));
    }
}
