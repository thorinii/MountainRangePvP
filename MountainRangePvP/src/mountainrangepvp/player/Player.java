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

    public static final int WIDTH = 40;
    public static final int HEIGHT = 100;
    public static final float WALK_SPEED = 1000;
    public static final float WALK_ACCELERATION = 15;
    public static final float AIR_SPEED = 500;
    public static final float AIR_ACCELERATION = 15;
    public static final float FRICTION = 0.1f;
    public static final float JUMP_SPEED = 550f;
    public static final int MAX_WALK_SLOPE = 10;
    public static final int MIN_SLIDE_SLOPE = 30;
    public static final int MAX_SLIDE_SLOPE = 80;
    public static final int RESPAWN_TIMEOUT = 2000;
    public static final int RESPAWN_RANGE_X = 1000;
    //
    private final String name;
    private final Vector2 position;
    private final Vector2 velocity;
    private final Vector2 gunDirection;
    private boolean onGround;
    //
    private boolean alive;
    private int respawnTimer;

    public Player(String name) {
        this.name = name;

        this.position = new Vector2();
        this.velocity = new Vector2();
        this.gunDirection = new Vector2();

        this.onGround = false;
        respawn();
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getCentralPosition() {
        return new Vector2(position.x + WIDTH / 2, position.y + HEIGHT / 2);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getGunDirection() {
        return gunDirection;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void kill() {
        if (alive) {
            alive = false;
            respawnTimer = 0;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public void updateRespawnTimer(float dt) {
        if (!alive) {
            this.respawnTimer += (int) (1000 * dt);

            if (respawnTimer > RESPAWN_TIMEOUT) {
                respawn();
            }
        }
    }

    private void respawn() {
        position.x = (float) (Math.random() * 2 * RESPAWN_RANGE_X - RESPAWN_RANGE_X) + 10000;
        position.y = 1000;
        velocity.x = 0;
        velocity.y = 0;

        alive = true;

        onGround = false;
    }
}
