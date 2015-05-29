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
    private final Team team;
    private final Vector2 position;

    public Old_Player(String name, Team team) {
        this.name = name;
        this.team = team;

        this.position = new Vector2(0, 0);
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Team getTeam() {
        return team;
    }

}
