package mountainrangepvp.shot;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import mountainrangepvp.mp.message.MessageListener;
import mountainrangepvp.player.Player;

/**
 *
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
