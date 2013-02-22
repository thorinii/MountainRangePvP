/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.shot;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import mountainrangepvp.terrain.HeightMap;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class ShotManager {

    public static final int MAX_SHOT_LIFE = 5;
    //
    private final List<Shot> shots;
    private final HeightMap heightMap;
    private final PlayerManager playerManager;
    private final List<ShotListener> listeners;

    public ShotManager(HeightMap heightMap, PlayerManager playerManager) {
        shots = new LinkedList<>();
        this.heightMap = heightMap;
        this.playerManager = playerManager;
        listeners = new ArrayList<>();
    }

    public void addShotListener(ShotListener listener) {
        listeners.add(listener);
    }

    public void removeShotListener(ShotListener listener) {
        listeners.remove(listener);
    }

    public void addShot(Vector2 base, Vector2 direction, Player player) {
        Shot shot = new Shot(base, direction, player);
        addShot(shot);
    }

    public void addShot(Shot shot) {
        shots.add(shot);

        for (ShotListener listener : listeners) {
            listener.shotAdd(shot);
        }
    }

    public void update(float dt) {
        Iterator<Shot> itr = shots.iterator();
        while (itr.hasNext()) {
            Shot shot = itr.next();
            Vector2 pos = shot.position();
            Vector2 npos = shot.position(shot.time + dt);

            if (testLife(shot)) {
                itr.remove();
            } else if (testTerrain(shot, pos, npos)) {
                itr.remove();

                for (ShotListener listener : listeners) {
                    listener.shotTerrainCollision(shot);
                }
            } else {
                Player hit = testPlayers(shot, pos, npos);
                if (hit != null) {
                    itr.remove();

                    if (!hit.isSpawnBubbleOn()) {
                        hit.kill();
                        shot.player.addHit();

                        for (ShotListener listener : listeners) {
                            listener.shotPlayerCollision(shot, hit);
                        }
                    } else {
                        // TODO: make ricochet
                    }
                }
            }

            shot.time += dt;
        }
    }

    private boolean testLife(Shot shot) {
        return shot.time > MAX_SHOT_LIFE;
    }

    private boolean testTerrain(Shot shot, Vector2 pos, Vector2 npos) {
        if (shot.direction.x > 0) {
            int[] block = heightMap.getBlock((int) pos.x,
                                             (int) Math.ceil(npos.x - pos.x));

            float w = block.length;
            for (int i = 0; i < block.length; i++) {
                if (block[i] >= (w - i) / w * pos.y + i / w * npos.y) {
                    return true;
                }
            }
        } else {
            int[] block = heightMap.getBlock((int) npos.x,
                                             (int) Math.ceil(pos.x - npos.x));

            float w = block.length;
            for (int i = block.length - 1; i >= 0; i--) {
                if (block[i] >= (w - i) / w * pos.y + i / w * npos.y) {
                    return true;
                }
            }
        }

        return false;
    }

    private Player testPlayers(Shot shot, Vector2 pos, Vector2 npos) {
        Vector2 shot1 = pos;
        Vector2 shot2 = npos;

        Vector2 p1, p2;
        for (Player player : playerManager.getPlayers()) {
            if (!player.isAlive() || player == shot.player) {
                continue;
            }

            p1 = player.getPosition().cpy();
            p2 = p1.cpy().add(0, Player.HEIGHT);
            if (Intersector.intersectSegments(shot1, shot2, p1, p2, null)) {
                return player;
            }

            p2 = p1.cpy().add(Player.WIDTH, 0);
            if (Intersector.intersectSegments(shot1, shot2, p1, p2, null)) {
                return player;
            }

            p1 = p1.add(Player.WIDTH, 0);
            p2 = p1.cpy().add(0, Player.HEIGHT);
            if (Intersector.intersectSegments(shot1, shot2, p1, p2, null)) {
                return player;
            }

            p1 = player.getPosition().cpy().add(0, Player.HEIGHT);
            p2 = p1.cpy().add(Player.WIDTH, 0);
            if (Intersector.intersectSegments(shot1, shot2, p1, p2, null)) {
                return player;
            }
        }

        return null;
    }

    public List<Shot> getShots() {
        return shots;
    }
}
