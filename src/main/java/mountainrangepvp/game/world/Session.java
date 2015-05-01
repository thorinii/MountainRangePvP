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
    private final boolean teamsOn;
    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    private Map map;
    private PlayerStats stats;

    public Session(Log log, EventBus eventBus, boolean teamsOn, PlayerManager playerManager, ChatManager chatManager) {
        this.log = log;
        this.eventBus = eventBus;
        this.teamsOn = teamsOn;
        this.playerManager = playerManager;
        this.chatManager = chatManager;

        this.map = null;
        this.stats = new PlayerStats();

        subscribeTo(eventBus);
    }

    private void subscribeTo(EventBus eventbus) {
        eventbus.subscribe(NewMapEvent.class, new NewMapHandler());
        eventbus.subscribe(PlayerStatsUpdatedEvent.class, new EventHandler<PlayerStatsUpdatedEvent>() {
            @Override
            public void receive(PlayerStatsUpdatedEvent event) {
                stats = event.stats();
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

    public PlayerStats getStats() {
        return stats;
    }

    public void update(float dt) {
        if (hasMap())
            map.update(dt);
    }


    private class NewMapHandler implements EventHandler<NewMapEvent> {
        @Override
        public void receive(NewMapEvent event) {
            log.info("Received seed " + event.seed() + "; changing map");

            HeightMap heightMap = new HillsHeightMap(event.seed());

            Terrain terrain = new Terrain(heightMap);
            ShotManager shotManager = new ShotManager(log, eventBus, playerManager, terrain, false, teamsOn);

            Session.this.map = new Map(shotManager, terrain, teamsOn);

            // TODO: shotManager.addShotListener(new AudioShotListener(playerManager, audioManager));
        }
    }
}
