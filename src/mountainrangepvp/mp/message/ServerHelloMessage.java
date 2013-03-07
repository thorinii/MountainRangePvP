package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.mp.MultiplayerConstants;

/**
 *
 * @author lachlan
 */
public class ServerHelloMessage implements Message {

    private int checkCode;
    private int version;
    private int clientID;

    public ServerHelloMessage() {
        checkCode = MultiplayerConstants.CHECK_CODE;
        version = MultiplayerConstants.VERSION;
    }

    public ServerHelloMessage(int clientID) {
        this();
        this.clientID = clientID;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(checkCode);
        dos.writeInt(version);
        dos.writeInt(clientID);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        checkCode = dis.readInt();
        version = dis.readInt();
        clientID = dis.readInt();
    }

    public boolean isValid() {
        return checkCode == MultiplayerConstants.CHECK_CODE
                && version == MultiplayerConstants.VERSION;
    }

    public int getClientID() {
        return clientID;
    }

    @Override
    public String toString() {
        return "ServerHelloMessage{checkCode=" + checkCode + ", version="
                + version + ", clientID=" + clientID + '}';
    }
}
