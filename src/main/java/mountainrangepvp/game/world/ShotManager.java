package mountainrangepvp.game.world;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.mp.message.MessageListener;

import java.util.List;

/**
 * @author lachlan
 */
public interface ShotManager extends MessageListener {

    public static final int MAX_SHOT_LIFE = 5;

    public void addShot(Vector2 base, Vector2 direction, Player player);

    public void addShot(Shot shot);

    public void addShotListener(ShotListener listener);

    public List<Shot> getShots();

    public void removeShotListener(ShotListener listener);

    public void update(float dt);
}
