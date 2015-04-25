package mountainrangepvp.game.world;

/**
 * State that doesn't change between maps.
 */
public class Session {
    public final PlayerManager playerManager;
    public final ChatManager chatManager;

    private Map map;

    public Session(PlayerManager playerManager, ChatManager chatManager) {
        this.playerManager = playerManager;
        this.chatManager = chatManager;

        this.map = null;
    }

    public Map getMap() {
        if (map == null)
            throw new IllegalStateException("No map loaded");
        return map;
    }

    public boolean hasMap() {
        return map != null;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void update(float dt) {
        if (hasMap())
            map.update(dt);
    }
}
