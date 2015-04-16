package mountainrangepvp.engine.util;

/**
 * Created by lachlan on 16/04/15.
 */
public interface EventHandler<T extends Event> {
    public void receive(T event);
}
