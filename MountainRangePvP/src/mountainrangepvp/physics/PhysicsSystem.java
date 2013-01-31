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

        vel.x = 20; // Testing X movement


        // Add Gravity        
        vel.y += GRAVITY * dt;


        // Apply the velocity
        pos.x += vel.x * dt;
        pos.y += vel.y * dt;


        // Ensure we aren't intersecting the terrain
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
}
