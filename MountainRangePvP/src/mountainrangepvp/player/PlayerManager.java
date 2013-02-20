/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author lachlan
 */
public class PlayerManager {

    private final List<Player> players;
    private final Player localPlayer;

    public PlayerManager(String localName) {
        players = new ArrayList<>();

        localPlayer = new Player(localName);
        players.add(localPlayer);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void addPlayer(String playerName) {
        players.add(new Player(playerName));
    }

    public void removePlayer(String playerName) {
        Iterator<Player> itr = players.iterator();
        while (itr.hasNext()) {
            Player p = itr.next();

            if (p.getName().equals(playerName)) {
                itr.remove();
            }
        }
    }

    public Player getPlayer(String playerName) {
        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                return p;
            }
        }

        return null;
    }

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
