package mountainrangepvp.game.world;

import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.engine.util.Log;

/**
 * State that doesn't change between maps.
 */
public class Session {
    private final boolean teamsOn;
    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    private Map map;

    public Session(EventBus eventbus, boolean teamsOn, PlayerManager playerManager, ChatManager chatManager) {
        this.teamsOn = teamsOn;
        this.playerManager = playerManager;
        this.chatManager = chatManager;

        this.map = null;

        eventbus.subscribe(NewMapEvent.class, new NewMapHandler());
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


    private class NewMapHandler implements EventHandler<NewMapEvent> {
        @Override
        public void receive(NewMapEvent event) {
            Log.info("Received Seed", event.seed(), "Changing Map");

            HeightMap heightMap = new HillsHeightMap(event.seed());

            Terrain terrain = new Terrain(heightMap);
            ShotManager shotManager = new ShotManager(playerManager, terrain, false, teamsOn);

            Session.this.map = new Map(shotManager, terrain, teamsOn);

            // TODO: shotManager.addShotListener(new AudioShotListener(playerManager, audioManager));
        }
    }
}
