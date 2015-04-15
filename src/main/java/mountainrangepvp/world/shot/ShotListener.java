package mountainrangepvp.world.shot;

import mountainrangepvp.world.player.Player;

/**
 * @author lachlan
 */
public interface ShotListener {

    public void shotAdd(Shot shot);

    public void shotTerrainCollision(Shot shot);

    public void shotPlayerCollision(Shot shot, Player player);
}
