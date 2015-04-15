package mountainrangepvp.game.mp.message;

import java.io.IOException;

/**
 * @author lachlan
 */
public interface MessageListener {

    public void accept(Message message, int id) throws IOException;
}
