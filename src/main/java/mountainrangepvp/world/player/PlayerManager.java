package mountainrangepvp.world.player;

import mountainrangepvp.mp.message.MessageListener;

import java.util.List;

/**
 * @author lachlan
 */
public interface PlayerManager extends MessageListener {

    public List<Player> getPlayers();

    public Player getLocalPlayer();

    public Player getPlayer(String playerName);

    public Player getPlayer(int id);

    public List<Player> getPlayersByHits(int count);
}
