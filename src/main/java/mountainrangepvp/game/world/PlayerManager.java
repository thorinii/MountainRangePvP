package mountainrangepvp.game.world;

import mountainrangepvp.game.mp.message.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lachlan
 */
public class PlayerManager implements MessageListener {

    protected final List<Old_Player> players;
    private final String localPlayerName;
    private final Old_Player.Team localPlayerTeam;
    private Old_Player localPlayer;

    public PlayerManager(String localName, Old_Player.Team localTeam) {
        players = new CopyOnWriteArrayList<>();
        localPlayerName = localName;
        localPlayerTeam = localTeam;
        localPlayer = new Old_Player(localName, 0, localTeam);
    }

    public Old_Player getLocalPlayer() {
        return localPlayer;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public Old_Player.Team getLocalPlayerTeam() {
        return localPlayerTeam;
    }

    @Override
    public void accept(Message message, int id) throws IOException {
        if (message instanceof ServerHelloMessage) {
            ServerHelloMessage shm = (ServerHelloMessage) message;
            localPlayer = new Old_Player(localPlayerName, shm.getClientID(),
                                     localPlayerTeam);
            players.add(localPlayer);

        } else if (message instanceof PlayerConnectMessage) {
            PlayerConnectMessage pcm = (PlayerConnectMessage) message;
            players.add(new Old_Player(pcm.getPlayerName(), pcm.getID(),
                                   pcm.getTeam()));

        } else if (message instanceof PlayerDisconnectMessage) {
            PlayerDisconnectMessage pdm = (PlayerDisconnectMessage) message;
            players.remove(getPlayer(pdm.getID()));

        } else if (message instanceof PlayerUpdateMessage) {
            PlayerUpdateMessage pum = (PlayerUpdateMessage) message;
            Old_Player p = getPlayer(pum.getPlayer());

            if (p == null)
                return;

            p.setAlive(pum.isAlive());
            p.getPosition().set(pum.getPos());
            p.getVelocity().set(pum.getVel());
            p.getGunDirection().set(pum.getGun());
            p.setHits(pum.getHits());

        } else if (message instanceof PlayerDeathMessage) {
            PlayerDeathMessage pdm = (PlayerDeathMessage) message;

            Old_Player hit = getPlayer(pdm.getHitID());
            Old_Player hitter = getPlayer(pdm.getHitterID());


            if (hit == null || hitter == null)
                return;

            hit.kill();
            hitter.addHit();
        }
    }

    public List<Old_Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Old_Player getPlayer(String playerName) {
        for (Old_Player p : players) {
            if (p.getName().equals(playerName)) {
                return p;
            }
        }

        return null;
    }

    public Old_Player getPlayer(int id) {
        for (Old_Player player : players) {
            if (player.getID() == id)
                return player;
        }
        return null;
    }

    public List<Old_Player> getPlayersByHits(int count) {
        List<Old_Player> tmp = new ArrayList<>(players);

        Collections.sort(tmp, new Comparator<Old_Player>() {
            @Override
            public int compare(Old_Player o1, Old_Player o2) {
                return o2.getHits() - o1.getHits();
            }
        });

        return tmp.subList(0, Math.min(tmp.size(), count));
    }
}
