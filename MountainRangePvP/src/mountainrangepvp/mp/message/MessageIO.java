/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import mountainrangepvp.Log;
import mountainrangepvp.mp.MultiplayerConstants;

/**
 *
 * @author lachlan
 */
public class MessageIO {

    private final DataInputStream dis;
    private final DataOutputStream dos;

    public MessageIO(Socket socket) throws IOException {
        this(socket.getInputStream(), socket.getOutputStream());
    }

    public MessageIO(InputStream in, OutputStream out) {
        dis = new DataInputStream(in);
        dos = new DataOutputStream(out);
    }

    /**
     * Sends a message over the stream.
     * <p/>
     * @param message
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        dos.writeInt(message.getCode());
        message.writeOut(dos);
        dos.flush();

        Log.fine("Sent Message ", message.getClass());
    }

    /**
     * Blocking reads a message from the stream.
     * <p/>
     * @return
     * @throws IOException
     */
    public Message readMessage() throws IOException {
        int messageCode = dis.readInt();

        Message message = getMessageByCode(messageCode);
        message.readIn(dis);

        Log.fine("Read Message ", message.getClass());

        return message;
    }

    private Message getMessageByCode(int code) {
        switch (code) {
            case MultiplayerConstants.MESSAGE_HELLO:
                return new HelloMessage();
            case MultiplayerConstants.MESSAGE_SEED:
                return new SeedMessage();
            case MultiplayerConstants.MESSAGE_PLAYER_CONNECT:
                return new PlayerConnectMessage();
        }

        throw new IllegalArgumentException(
                "Could not find message for code: " + code);
    }
}
