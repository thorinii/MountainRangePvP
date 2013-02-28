/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.shot;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class Shot {

    public static final int SHOT_SPEED = 2000;
    public final Vector2 base;
    public final Vector2 direction;
    public final Player player;
    public float time;

    public Shot(Vector2 base, Vector2 direction, Player player) {
        this.base = base;
        this.direction = direction;
        this.player = player;
        this.time = 0;
    }

    public Vector2 position() {
        return position(time);
    }

    public Vector2 position(float time) {
        return base.cpy().add(direction.cpy().mul(SHOT_SPEED * time));
    }
}