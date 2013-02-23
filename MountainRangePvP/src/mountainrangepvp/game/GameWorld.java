/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.ShotManager;
import mountainrangepvp.terrain.Terrain;

/**
 *
 * @author lachlan
 */
public class GameWorld {

    private Terrain terrain;
    private PlayerManager playerManager;
    private ShotManager shotManager;

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public ShotManager getShotManager() {
        return shotManager;
    }

    public void setShotManager(ShotManager shotManager) {
        this.shotManager = shotManager;
    }

    public void update(float dt) {
        // TODO: remove this
        if (shotManager != null)
            shotManager.update(dt);
    }
}
