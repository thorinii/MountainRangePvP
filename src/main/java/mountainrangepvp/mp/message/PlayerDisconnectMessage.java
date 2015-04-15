package mountainrangepvp.mp.message;

import mountainrangepvp.world.player.Player;

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

    public PlayerDisconnectMessage(Player player) {
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
