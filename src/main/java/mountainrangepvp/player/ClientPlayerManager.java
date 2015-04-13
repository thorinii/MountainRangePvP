package mountainrangepvp.player;

import java.io.IOException;
import java.util.*;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.player.Player.Team;

/**
 *
 * @author lachlan
 */
public class ClientPlayerManager extends AbstractPlayerManager {

    private final String localPlayerName;
    private final Team localPlayerTeam;
    private Player localPlayer;

    public ClientPlayerManager(String localName, Team localTeam) {
        localPlayerName = localName;
        localPlayerTeam = localTeam;
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
