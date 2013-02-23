/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import mountainrangepvp.mp.message.*;

/**
 *
 * @author lachlan
 */
public class ClientPlayerManager implements PlayerManager {

    private final List<Player> players;
    private final String localPlayerName;
    private Player localPlayer;

    public ClientPlayerManager(String localName) {
        players = new ArrayList<>();
        localPlayerName = localName;
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public Player getLocalPlayer() {
        return localPlayer;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    @Deprecated
    @Override
    public void addPlayer(String playerName) {
        players.add(new Player(playerName, -1));
    }

    @Deprecated
    @Override
    public void removePlayer(String playerName) {
        Iterator<Player> itr = players.iterator();
        while (itr.hasNext()) {
            Player p = itr.next();

            if (p.getName().equals(playerName)) {
                itr.remove();
            }
        }
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

    @Override
    public void update(float dt) {
    }

    @Override
    public void accept(Message message, int id) throws IOException {
        if (message instanceof ServerHelloMessage) {
            ServerHelloMessage shm = (ServerHelloMessage) message;
            localPlayer = new Player(localPlayerName, shm.getClientID());
            players.add(localPlayer);

        } else if (message instanceof PlayerConnectMessage) {
            PlayerConnectMessage pcm = (PlayerConnectMessage) message;
            players.add(new Player(pcm.getPlayerName(), pcm.getID()));

        } else if (message instanceof PlayerDisconnectMessage) {
            PlayerDisconnectMessage pdm = (PlayerDisconnectMessage) message;
            players.remove(getPlayer(pdm.getID()));

        } else if (message instanceof PlayerUpdateMessage) {
            PlayerUpdateMessage pum = (PlayerUpdateMessage) message;
            Player p = getPlayer(pum.getPlayer());

            if (p == null)
                return;

            p.setAlive(pum.isAlive());
            p.getPosition().set(pum.getPos());
            p.getVelocity().set(pum.getVel());
            p.getGunDirection().set(pum.getGun());
            p.setHits(pum.getHits());
        } else if (message instanceof PlayerDeathMessage) {
            PlayerDeathMessage pdm = (PlayerDeathMessage) message;

            Player hit = getPlayer(pdm.getHitID());
            Player hitter = getPlayer(pdm.getHitterID());

            hit.kill();
            hitter.addHit();
        }
    }
}
