package mountainrangepvp.game.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lachlan
 */
public abstract class AbstractPlayerManager implements PlayerManager {

    protected final List<Player> players;

    public AbstractPlayerManager() {
        players = new CopyOnWriteArrayList<>();
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public Player getPlayer(String playerName) {
        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                return p;
            }
        }

        return null;
    }

    @Override
    public Player getPlayer(int id) {
        for (Player player : players) {
            if (player.getID() == id)
                return player;
        }
        return null;
    }

    @Override
    public List<Player> getPlayersByHits(int count) {
        List<Player> tmp = new ArrayList<>(players);

        Collections.sort(tmp, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.getHits() - o1.getHits();
            }
        });

        return tmp.subList(0, Math.min(tmp.size(), count));
    }
}