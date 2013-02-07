/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import java.util.ArrayList;
import java.util.Collections;
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
}
