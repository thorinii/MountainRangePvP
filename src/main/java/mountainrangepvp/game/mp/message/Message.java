package mountainrangepvp.game.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public interface Message {

    public void writeOut(DataOutputStream dos) throws IOException;

    public void readIn(DataInputStream dis) throws IOException;
}
