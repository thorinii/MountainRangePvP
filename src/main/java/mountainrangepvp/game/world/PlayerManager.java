package mountainrangepvp.game.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lachlan
 */
public class PlayerManager {

    protected final List<Old_Player> players;
    private Old_Player localPlayer;

    public PlayerManager(String localName, Old_Player.Team localTeam) {
        players = new CopyOnWriteArrayList<>();
        localPlayer = new Old_Player(localName, 0, localTeam);
    }

    public Old_Player getLocalPlayer() {
        return localPlayer;
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
