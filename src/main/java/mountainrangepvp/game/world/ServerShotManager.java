package mountainrangepvp.game.world;

import mountainrangepvp.util.Log;

/**
 * @author lachlan
 */
public class ServerShotManager extends AbstractShotManager {

    public ServerShotManager(Instance instance) {
        super(instance);
    }

    @Override
    protected void handlePlayerHit(Shot shot, Player hit) {
        Log.fine(hit + " (server) was shot");
        instance.chatManager.addLine(hit.getName() + " was hit.");

        hit.kill();
        shot.player.addHit();
    }
}
