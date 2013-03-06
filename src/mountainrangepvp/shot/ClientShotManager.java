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
public class ClientShotManager extends AbstractShotManager {

    public ClientShotManager(GameWorld world) {
        super(world);
    }

    @Override
    protected void handlePlayerHit(Shot shot, Player hit) {
        hit.kill();
    }
}
