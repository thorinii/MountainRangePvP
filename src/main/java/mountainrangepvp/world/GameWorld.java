package mountainrangepvp.world;

import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ShotManager;
import mountainrangepvp.world.terrain.Terrain;

/**
 * @author lachlan
 */
public class GameWorld {

    public final PlayerManager playerManager;
    public final ShotManager shotManager;
    public final ChatManager chatManager;
    public final Terrain terrain;
    public final boolean teamModeOn;

    public GameWorld(PlayerManager playerManager, ShotManager shotManager, ChatManager chatManager, Terrain terrain, boolean teamModeOn) {
        this.playerManager = playerManager;
        this.shotManager = shotManager;
        this.chatManager = chatManager;
        this.terrain = terrain;
        this.teamModeOn = teamModeOn;
    }

    public void update(float dt) {
        shotManager.update(dt);
    }
}
