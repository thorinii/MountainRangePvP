/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import java.util.List;

/**
 *
 * @author lachlan
 */
public interface PlayerManager {

    public List<Player> getPlayers();

    public Player getLocalPlayer();
}
