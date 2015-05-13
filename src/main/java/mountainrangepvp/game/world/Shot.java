package mountainrangepvp.game.world;

import com.badlogic.gdx.math.Vector2;

/**
 * @author lachlan
 */
public class Shot {

    public static final int SHOT_SPEED = 2000;
    public final Vector2 base;
    public final Vector2 direction;
    public final Old_Player player;
    public float time;

    public Shot(Vector2 base, Vector2 direction, Old_Player player) {
        this.base = base;
        this.direction = direction;
        this.player = player;
        this.time = 0;
    }

    public Vector2 position() {
        return position(time);
    }

    public Vector2 position(float time) {
        return base.cpy().add(direction.cpy().scl(SHOT_SPEED * time));
    }
}
