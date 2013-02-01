/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.physics;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class PhysicsSystem {

    private static final float GRAVITY = -1000f;
    private static final float DAMPING = 0.01f;
    //
    private final HeightMap heightMap;
    private final PlayerManager playerManager;

    public PhysicsSystem(HeightMap heightMap, PlayerManager playerManager) {
        this.heightMap = heightMap;
        this.playerManager = playerManager;
    }

    public void update(float dt) {
        for (Player player : playerManager.getPlayers()) {
            updatePlayer(player, dt);
        }
    }

    private void updatePlayer(Player player, float dt) {
        Vector2 pos = player.getPosition();
        Vector2 vel = player.getVelocity();

        dampenVelocity(vel, dt);

        // Add Gravity
        vel.y += GRAVITY * dt;

        // Apply the velocity
        pos.x += vel.x * dt;
        pos.y += vel.y * dt;

        checkGroundIntersection(player, pos, vel);
    }

    private void checkGroundIntersection(Player player, Vector2 pos, Vector2 vel) {
        player.setOnGround(false);

        int[] block = heightMap.getBlock((int) pos.x, Player.WIDTH);
        for (int i = 0; i < block.length; i++) {
            if (pos.y < block[i]) {
                pos.y = block[i];
                vel.y = 0;

                player.setOnGround(true);
            }
        }
    }

    private void dampenVelocity(Vector2 vel, float dt) {
        vel.x -= vel.x * DAMPING * dt;
        vel.y -= vel.y * DAMPING * dt;

        if (vel.x < 1 && vel.x > -1) {
            vel.x = 0;
        }
    }
}
