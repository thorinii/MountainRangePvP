package mountainrangepvp.game.event;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.util.Event;
import mountainrangepvp.game.world.Player;

/**
 * The player fired a weapon.
 */
public class PlayerFiredEvent extends Event {
    public final Player player;
    public final Vector2 source, direction;

    public PlayerFiredEvent(Player player, Vector2 source, Vector2 direction) {
        this.player = player;
        this.source = source;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "PlayerFiredEvent{" +
                "player=" + player +
                ", source=" + source +
                ", direction=" + direction +
                '}';
    }
}
