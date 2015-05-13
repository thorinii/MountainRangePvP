package mountainrangepvp.game.world;

import com.badlogic.gdx.math.Vector2;

/**
 * @author lachlan
 */
public class PhysicsSystem {

    private static final float GRAVITY = -1000f;
    private static final float DAMPING = 0.01f;

    public void update(Session world, float dt) {
        for (Old_Player player : world.playerManager.getPlayers()) {
            updatePlayer(world.getMap().terrain, player, dt);
        }
    }

    /**
     * Updates the player's velocity and position, with collision detection.
     * A bit of fun when the player dies: turn off collision detection.
     * <p/>
     *
     * @param player
     * @param dt
     */
    private void updatePlayer(Terrain terrain, Old_Player player, float dt) {
        Vector2 pos = player.getPosition();
        Vector2 vel = player.getVelocity();

        //dampenVelocity(vel, dt);

        checkWalkUpSlope(terrain, vel, pos, dt);
        //slideDownSlope(player, pos, vel, dt);

        vel.y += GRAVITY * dt;

        pos.x += vel.x * dt;
        pos.y += vel.y * dt;

        checkGroundIntersection(terrain, player, pos, vel);

        player.update();

    }

    private void checkWalkUpSlope(Terrain terrain, Vector2 vel, Vector2 pos, float dt) {
        int base, length;
        if (vel.x < 0) {
            base = (int) (pos.x + vel.x * dt);
            length = (int) Math.ceil(-vel.x * dt) + 1;
        } else {
            base = (int) pos.x + Old_Player.WIDTH;
            length = (int) Math.ceil(vel.x * dt) + 1;
        }

        int highest = terrain.getHighestPointBetween(base, base + length);
        int slope = highest - (int) pos.y;


        if (slope > Old_Player.MAX_WALK_SLOPE) {
            vel.x = 0;
        } else if (slope > 0) {
            vel.y += slope * 190 * dt;
        }
    }

    private void slideDownSlope(Terrain terrain, Old_Player player, Vector2 pos, Vector2 vel,
                                float dt) {
        if (player.isOnGround()) {
            Terrain.Slice slice = terrain.getSlice((int) pos.x - 1, Old_Player.WIDTH + 3);

            int maxIndex = slice.getHighestIndex();
            int maxHeight = slice.getHighestPoint();

            if (maxIndex == 1) { // left corner
                int slope = (int) pos.y - maxHeight;
                if (slope > Old_Player.MIN_SLIDE_SLOPE) {
                    // Slide right
                    vel.x += 500 * dt;
                    vel.y -= 50 * dt;
                }
            } else if (maxIndex == Old_Player.WIDTH + 2) { // right corner
                int slope = (int) pos.y - maxHeight;
                if (slope > Old_Player.MIN_SLIDE_SLOPE) {
                    // Slide left
                    vel.x -= 500 * dt;
                    vel.y -= 50 * dt;
                }
            }
        }
    }

    private void checkGroundIntersection(Terrain terrain, Old_Player player, Vector2 pos, Vector2 vel) {
        Terrain.Slice slice = terrain.getSlice((int) pos.x, Old_Player.WIDTH);

        int highestPoint = slice.getHighestPoint();

        boolean onGround = false;
        if (highestPoint > pos.y) {
            pos.y = highestPoint;

            if (vel.y < 0) {
                vel.y = 0.2f * -vel.y;
            }

            if (player.isAlive())
                onGround = true;
        }

        player.setOnGround(onGround);
    }

    private void dampenVelocity(Vector2 vel, float dt) {
        vel.x -= vel.x * DAMPING * dt;
        vel.y -= vel.y * DAMPING * dt;

        if (vel.x < 1 && vel.x > -1) {
            vel.x = 0;
        }
    }
}
