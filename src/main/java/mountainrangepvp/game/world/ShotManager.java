package mountainrangepvp.game.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.mp.message.Message;
import mountainrangepvp.game.mp.message.MessageListener;
import mountainrangepvp.game.mp.message.NewShotMessage;
import mountainrangepvp.util.Log;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lachlan
 */
public class ShotManager implements MessageListener {
    public static final int MAX_SHOT_LIFE = 5;

    private final PlayerManager playerManager;
    private final Terrain terrain;
    private final boolean addPoints, teamMode;

    private final List<ShotListener> listeners;
    private final List<Shot> shots;

    public ShotManager(PlayerManager playerManager, Terrain terrain, boolean addPoints, boolean teamMode) {
        this.playerManager = playerManager;
        this.terrain = terrain;
        this.addPoints = addPoints;
        this.teamMode = teamMode;

        listeners = new ArrayList<>();
        shots = new LinkedList<>();
    }

    public void addShotListener(ShotListener listener) {
        listeners.add(listener);
    }

    public void removeShotListener(ShotListener listener) {
        listeners.remove(listener);
    }

    public void addShot(Vector2 base, Vector2 direction, Player player) {
        addShot(new Shot(base, direction, player));
    }

    public void addShot(Shot shot) {
        shots.add(shot);
        fireShotAdd(shot);
    }

    public List<Shot> getShots() {
        return shots;
    }

    public final void update(float dt) {
        for (Shot shot : new ArrayList<>(shots)) {
            if (handleShot(shot, dt))
                shots.remove(shot);
        }
    }

    private boolean handleShot(Shot shot, float dt) {
        Vector2 pos = shot.position();
        Vector2 npos = shot.position(shot.time + dt);

        shot.time += dt;

        if (testLife(shot)) {
            return true;
        } else if (testTerrain(pos, npos)) {
            fireShotTerrainCollision(shot);
            return true;
        } else {
            Player hit = testNewRespawnPlayers(shot, pos, npos);
            if (hit != null) {
                if (teamMode && hit.getTeam() == shot.player.getTeam()) {
                    // Let it pass
                } else {
                    Vector2 intersection =
                            getLineCircleIntersection(pos, npos,
                                                      hit.getCentralPosition(),
                                                      Player.SPAWN_BUBBLE_RADIUS + 1);
                    Vector2 direction = intersection.cpy().sub(hit.
                            getCentralPosition()).nor();

                    Shot ricochet = new Shot(intersection, direction,
                                             hit);
                    shots.add(ricochet);

                    return true;
                }
            } else {
                hit = testNonRespawnPlayers(shot, pos, npos);
                if (hit != null)
                    if (teamMode && hit.getTeam() == shot.player.getTeam()) {
                        // Let it pass
                    } else {
                        handlePlayerHit(shot, hit);
                        fireShotPlayerCollision(shot, hit);
                        return true;
                    }
            }
        }

        return false;
    }

    private boolean testLife(Shot shot) {
        return shot.time > MAX_SHOT_LIFE;
    }

    private boolean testTerrain(Vector2 pos, Vector2 npos) {
        return terrain.collideLine(pos, npos);
    }

    private Player testNewRespawnPlayers(Shot shot, Vector2 lp1, Vector2 lp2) {
        for (Player player : playerManager.getPlayers()) {
            if (!player.isAlive() || player == shot.player || !player.
                    isSpawnBubbleOn()) {
                continue;
            }

            Vector2 pp = player.getCentralPosition();

            if (Line2D.ptSegDist(lp1.x, lp1.y, lp2.x, lp2.y, pp.x, pp.y) <= Player.SPAWN_BUBBLE_RADIUS) {
                return player;
            }
        }

        return null;
    }

    private Player testNonRespawnPlayers(Shot shot, Vector2 pos, Vector2 npos) {
        Vector2 shot1 = pos;
        Vector2 shot2 = npos;

        Vector2 p1, p2;
        for (Player player : playerManager.getPlayers()) {
            if (!player.isAlive() || player == shot.player || player.
                    isSpawnBubbleOn()) {
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

    private Vector2 getLineCircleIntersection(Vector2 l1, Vector2 l2,
                                              Vector2 circle, float radius) {
        float baX = l2.x - l1.x;
        float baY = l2.y - l1.y;
        float caX = circle.x - l1.x;
        float caY = circle.y - l1.y;

        float a = baX * baX + baY * baY;
        float bBy2 = baX * caX + baY * caY;
        float c = caX * caX + caY * caY - radius * radius;

        float pBy2 = bBy2 / a;
        float q = c / a;

        float disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return null;
        }

        // if disc == 0 ... dealt with later
        float tmpSqrt = (float) Math.sqrt(disc);
        float abScalingFactor1 = -pBy2 + tmpSqrt;
        float abScalingFactor2 = -pBy2 - tmpSqrt;

        Vector2 i1 = new Vector2(l1.x - baX * abScalingFactor1, l1.y
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return i1;
        }

        Vector2 i2 = new Vector2(l1.x - baX * abScalingFactor2, l1.y
                - baY * abScalingFactor2);
        if (l1.dst(i1) < l1.dst(i2))
            return i1;
        else
            return i2;
    }

    protected void handlePlayerHit(Shot shot, Player hit) {
        Log.fine(hit + " was shot");
        hit.kill();
        if (addPoints)
            shot.player.addHit();
    }

    private void fireShotAdd(Shot shot) {
        for (ShotListener listener : listeners) {
            listener.shotAdd(shot);
        }
    }

    private void fireShotTerrainCollision(Shot shot) {
        for (ShotListener listener : listeners) {
            listener.shotTerrainCollision(shot);
        }
    }

    private void fireShotPlayerCollision(Shot shot, Player hit) {
        for (ShotListener listener : listeners) {
            listener.shotPlayerCollision(shot, hit);
        }
    }

    @Override
    public void accept(Message message, int id) throws IOException {
        if (message instanceof NewShotMessage) {
            NewShotMessage nsm = (NewShotMessage) message;
            addShot(nsm.getShot(playerManager));
        }
    }
}
