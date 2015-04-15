package mountainrangepvp.game.world;

import mountainrangepvp.util.Log;

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
