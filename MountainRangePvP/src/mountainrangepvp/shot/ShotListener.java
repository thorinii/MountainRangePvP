/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.shot;

import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public interface ShotListener {

    public void shotAdd(Shot shot);

    public void shotTerrainCollision(Shot shot);

    public void shotPlayerCollision(Shot shot, Player player);
}
