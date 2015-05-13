package mountainrangepvp.game.mp.message;

import mountainrangepvp.game.world.Old_Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public class PlayerDisconnectMessage implements Message {

    private int id;

    public PlayerDisconnectMessage() {
    }

    public PlayerDisconnectMessage(Old_Player player) {
        this(player.getID());
    }

    public PlayerDisconnectMessage(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(id);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        id = dis.readInt();
    }

    @Override
    public String toString() {
        return "PlayerDisconnect[" + id + "]";
    }
}
