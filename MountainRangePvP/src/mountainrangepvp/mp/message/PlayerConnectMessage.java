/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class PlayerConnectMessage implements Message {

    private String playerName;
    private int id;

    public PlayerConnectMessage() {
    }

    public PlayerConnectMessage(Player player) {
        this(player.getName(), player.getID());
    }

    public PlayerConnectMessage(String playerName, int id) {
        this.playerName = playerName;
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getID() {
        return id;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(playerName);
        dos.writeInt(id);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        playerName = dis.readUTF();
        id = dis.readInt();
    }

    @Override
    public String toString() {
        return "PlayerConnect[" + playerName + "; id=" + id + "]";
    }
}
