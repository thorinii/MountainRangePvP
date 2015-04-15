package mountainrangepvp.world.shot;

import mountainrangepvp.util.Log;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.world.player.Player;

/**
 * @author lachlan
 */
public class ServerShotManager extends AbstractShotManager {

    public ServerShotManager(GameWorld world) {
        super(world);
    }

    @Override
    protected void handlePlayerHit(Shot shot, Player hit) {
        Log.fine(hit + " (server) was shot");
        world.getChatManager().addLine(hit.getName() + " was hit.");

        hit.kill();
        shot.player.addHit();
    }
}
