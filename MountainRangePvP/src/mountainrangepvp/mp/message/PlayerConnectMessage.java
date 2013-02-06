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
public class PlayerConnectMessage implements Message {

    private String playerName;

    public PlayerConnectMessage() {
    }

    public PlayerConnectMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public int getCode() {
        return MultiplayerConstants.MESSAGE_PLAYER_CONNECT;
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
