/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.chat.ChatLine;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class NewChatMessage implements Message {

    private int id;
    private String message;

    public NewChatMessage() {
    }

    public NewChatMessage(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public NewChatMessage(ChatLine line) {
        this((line.getPlayer() == null) ? 0 : line.getPlayer().getID(), line.
                getText());
    }

    public int getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public ChatLine getLine(PlayerManager playerManager) {
        if (id == 0)
            return new ChatLine(null, message);
        return new ChatLine(playerManager.getPlayer(id), message);
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(id);
        dos.writeUTF(message);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        id = dis.readInt();
        message = dis.readUTF();
    }
}
