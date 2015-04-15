package mountainrangepvp.world;

import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ShotManager;
import mountainrangepvp.world.terrain.Terrain;

/**
 * @author lachlan
 */
public class Map {

    public final ShotManager shotManager;
    public final Terrain terrain;
    public final boolean teamModeOn;

    public Map(ShotManager shotManager, Terrain terrain, boolean teamModeOn) {
        this.shotManager = shotManager;
        this.terrain = terrain;
        this.teamModeOn = teamModeOn;
    }

    public void update(float dt) {
        shotManager.update(dt);
    }
}
