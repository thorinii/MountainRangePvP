package mountainrangepvp.game.mp.message;

import mountainrangepvp.game.mp.MultiplayerConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public class ClientHelloMessage implements Message {

    private int checkCode;
    private int version;

    public ClientHelloMessage() {
        checkCode = MultiplayerConstants.CHECK_CODE;
        version = MultiplayerConstants.VERSION;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(checkCode);
        dos.writeInt(version);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        checkCode = dis.readInt();
        version = dis.readInt();
    }

    public boolean isValid() {
        return checkCode == MultiplayerConstants.CHECK_CODE
                && version == MultiplayerConstants.VERSION;
    }

    @Override
    public String toString() {
        return "ClientHelloMessage{checkCode=" + checkCode + ", version=" + version + '}';
    }
}
