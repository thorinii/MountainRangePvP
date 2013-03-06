/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import java.io.IOException;
import java.util.*;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.player.Player.Team;

/**
 *
 * @author lachlan
 */
public class ClientPlayerManager implements PlayerManager {

    private final List<Player> players;
    private final String localPlayerName;
    private final Team localPlayerTeam;
    private Player localPlayer;

    public ClientPlayerManager(String localName, Team localTeam) {
        players = new ArrayList<>();
        localPlayerName = localName;
        localPlayerTeam = localTeam;
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

    public Team getLocalPlayerTeam() {
        return localPlayerTeam;
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
            localPlayer = new Player(localPlayerName, shm.getClientID(),
                                     localPlayerTeam);
            players.add(localPlayer);

        } else if (message instanceof PlayerConnectMessage) {
            PlayerConnectMessage pcm = (PlayerConnectMessage) message;
            players.add(new Player(pcm.getPlayerName(), pcm.getID(),
                                   pcm.getTeam()));

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


            if (hit == null || hitter == null)
                return;

            hit.kill();
            hitter.addHit();
        }
    }
}
