/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.shot;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mountainrangepvp.generator.HeightMap;

/**
 *
 * @author lachlan
 */
public class ShotManager {

    public static final int GUN_RATE = 300;
//
    private final List<Shot> shots;
    private final HeightMap heightMap;

    public ShotManager(HeightMap heightMap) {
        shots = new ArrayList<>();
        this.heightMap = heightMap;
    }

    public void addShot(Vector2 base, Vector2 direction) {
        Shot shot = new Shot(base, direction);
        shots.add(shot);
    }

    public void update(float dt) {
        Iterator<Shot> itr = shots.iterator();
        while (itr.hasNext()) {
            Shot shot = itr.next();
            shot.time += dt;

            if (shot.time > 5) {
                itr.remove();
            }
        }
    }

    public List<Shot> getShots() {
        return shots;
    }

    public class Shot {

        public static final int SHOT_SPEED = 1000;
        public final Vector2 base, direction;
        public float time;

        public Shot(Vector2 base, Vector2 direction) {
            this.base = base;
            this.direction = direction;
            this.time = 0;
        }
    }
}
