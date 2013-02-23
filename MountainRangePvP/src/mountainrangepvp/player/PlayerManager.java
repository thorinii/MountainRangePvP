/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import java.util.List;
import mountainrangepvp.mp.message.MessageListener;

/**
 *
 * @author lachlan
 */
public interface PlayerManager extends MessageListener {

    public List<Player> getPlayers();

    public Player getLocalPlayer();

    @Deprecated
    public void addPlayer(String playerName);

    @Deprecated
    public void removePlayer(String playerName);

    public Player getPlayer(String playerName);

    public Player getPlayer(int id);

    public List<Player> getPlayersByHits(int count);

    public void update(float dt);
}
