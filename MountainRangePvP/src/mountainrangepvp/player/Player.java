/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author lachlan
 */
public class Player {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 60;
    public static final float WALK_SPEED = 100;
    public static final float AIR_SPEED = 10;
    public static final float FRICTION = 0.3f;
    public static final int MAX_WALK_SLOPE = 5;
    public static final int MIN_SLIDE_SLOPE = 10;
    //
    private final String name;
    private final Vector2 position;
    private final Vector2 velocity;
    private boolean onGround;

    public Player(String name) {
        this.name = name;

        this.position = new Vector2();
        this.velocity = new Vector2();
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
