package mountainrangepvp.world.shot;

import mountainrangepvp.util.Log;
import mountainrangepvp.world.Instance;
import mountainrangepvp.world.player.Player;

/**
 * @author lachlan
 */
public class ClientShotManager extends AbstractShotManager {

    public ClientShotManager(Instance instance) {
        super(instance);
    }

    @Override
    protected void handlePlayerHit(Shot shot, Player hit) {
        Log.fine(hit + " (client) was shot");
        hit.kill();
    }
}
