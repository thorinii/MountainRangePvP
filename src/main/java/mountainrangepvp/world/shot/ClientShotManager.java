package mountainrangepvp.world.shot;

import mountainrangepvp.util.Log;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.world.player.Player;

/**
 * @author lachlan
 */
public class ClientShotManager extends AbstractShotManager {

    public ClientShotManager(GameWorld world) {
        super(world);
    }

    @Override
    protected void handlePlayerHit(Shot shot, Player hit) {
        Log.fine(hit + " (client) was shot");
        hit.kill();
    }
}
