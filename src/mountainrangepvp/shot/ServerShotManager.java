/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.shot;

import mountainrangepvp.game.GameWorld;
import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class ServerShotManager extends AbstractShotManager {

    public ServerShotManager(GameWorld world) {
        super(world);
    }

    @Override
    protected void handlePlayerHit(Shot shot, Player hit) {
        hit.kill();
        shot.player.addHit();
    }
}
