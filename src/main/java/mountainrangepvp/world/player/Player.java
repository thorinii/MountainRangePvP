package mountainrangepvp.world.player;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.util.Timer;

/**
 * @author lachlan
 */
public class Player {

    public enum Team {

        ORANGE, RED, GREEN, BLUE
    }

    public static final int WIDTH = 40;
    public static final int HEIGHT = 100;
    public static final float WALK_SPEED = 1000;
    public static final float WALK_ACCELERATION = 100;
    public static final float AIR_SPEED = 1000;
    public static final float AIR_ACCELERATION = 80;
    public static final float FRICTION = 0.1f;
    public static final float JUMP_SPEED = 1050f;
    public static final float DOWN_ACCELERATION = 40f;
    public static final int MAX_WALK_SLOPE = 30;
    public static final int MIN_SLIDE_SLOPE = 50;
    public static final int MAX_SLIDE_SLOPE = 80;
    public static final int RESPAWN_TIMEOUT = 3000;
    public static final int RESPAWN_RANGE_X = 1000;
    public static final int SPAWN_BUBBLE_TIMEOUT = 8000;
    public static final int SPAWN_BUBBLE_RADIUS = 51;
    private static final int ON_GROUND_TIMEOUT = 3;

    private final String name;
    private final int id;
    private final Team team;
    private final Vector2 position;
    private final Vector2 velocity;
    private final Vector2 gunDirection;
    private int onGround;

    private boolean alive;
    private Timer timer;

    private int hits;

    public Player(String name, int id, Team team) {
        this.name = name;
        this.id = id;
        this.team = team;

        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.gunDirection = new Vector2(1, 0);

        this.onGround = 0;

        this.timer = new Timer();

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
        if (onGround) {
            this.onGround = Math.min(this.onGround + 1, ON_GROUND_TIMEOUT);
        } else {
            this.onGround = Math.max(this.onGround - 1, 0);
        }
    }

    public boolean isOnGround() {
        return onGround > 0;
    }

    public int getID() {
        return id;
    }

    public void kill() {
        if (alive) {
            alive = false;

            timer.reset();
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isSpawnBubbleOn() {
        return timer.getTime() < SPAWN_BUBBLE_TIMEOUT;
    }

    public Timer getRespawnTimer() {
        return timer;
    }

    public int getHits() {
        return hits;
    }

    public void addHit() {
        hits++;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public Team getTeam() {
        return team;
    }

    public void update() {
        timer.update();

        if (!alive) {
            if (timer.getTime() > RESPAWN_TIMEOUT) {
                respawn();
                timer.reset();
            }
        }
    }

    private void respawn() {
        position.x = (float) (Math.random() * 2 * RESPAWN_RANGE_X - RESPAWN_RANGE_X) + 10000;
        position.y = 1000;
        velocity.x = 0;
        velocity.y = 0;
        onGround = 0;

        alive = true;
    }
}
