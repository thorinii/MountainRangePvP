/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import mountainrangepvp.terrain.HeightMap;
import mountainrangepvp.terrain.Terrain;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class GameWorld {

    private Terrain terrain;
    private final PlayerManager playerManager;
    private final ShotManager shotManager;

    public GameWorld(Terrain terrain, PlayerManager playerManager,
            ShotManager shotManager) {
        this.terrain = terrain;
        this.playerManager = playerManager;
        this.shotManager = shotManager;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ShotManager getShotManager() {
        return shotManager;
    }
}
