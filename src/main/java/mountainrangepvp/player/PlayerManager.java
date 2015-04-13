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

    public Player getPlayer(String playerName);

    public Player getPlayer(int id);

    public List<Player> getPlayersByHits(int count);
}
