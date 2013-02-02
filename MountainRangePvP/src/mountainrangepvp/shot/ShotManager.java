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

    public static final int GUN_RATE = 100;
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

            if (shot.time > 5) {
                itr.remove();
            }

            Vector2 pos = shot.position();
            Vector2 npos = shot.position(shot.time + dt);
            if (shot.direction.x > 0) {
                int[] block = heightMap.getBlock((int) pos.x,
                                                 (int) Math.ceil(npos.x - pos.x));

                float w = block.length;
                for (int i = 0; i < block.length; i++) {
                    if (block[i] >= (w - i) / w * pos.y + i / w * npos.y) {
                        itr.remove();
                        break;
                    }
                }
            } else {
                int[] block = heightMap.getBlock((int) npos.x,
                                                 (int) Math.ceil(pos.x - npos.x));

                float w = block.length;
                for (int i = block.length - 1; i >= 0; i--) {
                    if (block[i] >= (w - i) / w * pos.y + i / w * npos.y) {
                        itr.remove();
                        break;
                    }
                }
            }

            shot.time += dt;
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

        public Vector2 position() {
            return position(time);
        }

        public Vector2 position(float time) {
            return base.cpy().add(direction.cpy().mul(SHOT_SPEED * time));
        }
    }
}
