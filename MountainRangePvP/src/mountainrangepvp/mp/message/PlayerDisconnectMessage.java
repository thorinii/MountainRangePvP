/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.mp.MultiplayerConstants;

/**
 *
 * @author lachlan
 */
public class PlayerDisconnectMessage implements Message {

    private String playerName;

    public PlayerDisconnectMessage() {
    }

    public PlayerDisconnectMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(playerName);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        playerName = dis.readUTF();
    }
}
