/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.player.Player;

/**
 *
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
