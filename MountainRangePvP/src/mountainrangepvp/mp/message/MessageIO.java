/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.*;
import java.lang.Class;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import mountainrangepvp.Log;
import mountainrangepvp.mp.message.Message;

/**
 *
 * @author lachlan
 */
public class MessageIO {

    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Map<String, Class<Message>> messageClasses;

    public MessageIO(Socket socket) throws IOException {
        this(socket.getInputStream(), socket.getOutputStream());
    }

    public MessageIO(InputStream in, OutputStream out) {
        dis = new DataInputStream(new BufferedInputStream(in));
        dos = new DataOutputStream(new BufferedOutputStream(out));

        messageClasses = new HashMap<>();
    }

    /**
     * Sends a message over the stream.
     * <p/>
     * @param message
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        dos.writeUTF(message.getClass().getName());
        message.writeOut(dos);
        dos.flush();
    }

    /**
     * Blocking reads a message from the stream.
     * <p/>
     * @return
     * @throws IOException
     */
    public Message readMessage() throws IOException {
        String messageClass = dis.readUTF();

        Message message = getMessageByClass(messageClass);
        message.readIn(dis);

        return message;
    }

    private Message getMessageByClass(String messageClass) {
        try {
            Class<Message> klass = messageClasses.get(messageClass);

            if (klass == null) {
                klass = (Class<Message>) Class.forName(messageClass);
                messageClasses.put(messageClass, klass);
            }

            return (Message) klass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Log.warn("Could not find message for class: ", messageClass, e);
            return null;
        }
    }
}
