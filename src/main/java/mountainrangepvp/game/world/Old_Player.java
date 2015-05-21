package mountainrangepvp.game.world;

import com.badlogic.gdx.math.Vector2;

/**
 * @author lachlan
 */
@Deprecated
public class Old_Player {

    public enum Team {

        ORANGE, RED, GREEN, BLUE
    }

    private final String name;
    private final int id;
    private final Team team;
    private final Vector2 position;

    private int hits;

    public Old_Player(String name, int id, Team team) {
        this.name = name;
        this.id = id;
        this.team = team;

        this.position = new Vector2(0, 0);
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getID() {
        return id;
    }

    public int getHits() {
        return hits;
    }

    public Team getTeam() {
        return team;
    }

}
