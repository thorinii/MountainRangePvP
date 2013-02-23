/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import mountainrangepvp.Log;

/**
 *
 * @author lachlan
 */
public abstract class Proxy implements Runnable {

    protected int id;
    protected final Socket socket;
    protected final MessageIO messageIO;
    protected final MessageQueue receiveQueue;
    protected final MessageQueue sendQueue;

    public Proxy(Socket socket, MessageQueue receiveQueue) throws
            IOException {
        this(-1, socket, receiveQueue);
    }

    public Proxy(int id, Socket socket, MessageQueue receiveQueue) throws
            IOException {
        this.id = id;
        this.socket = socket;
        this.messageIO = new MessageIO(socket);
        this.receiveQueue = receiveQueue;
        this.sendQueue = new MessageQueue();

        sendQueue.addListener(new SendMessageListener());
    }

    @Override
    public void run() {
        try {
            doNetworking();

            if (!socket.isClosed())
                // eg due to exceptions in the message handling
                socket.close();
        } catch (IOException ioe) {
            if (!socket.isClosed())
                Log.warn("Error reading messages:", ioe);
        }
    }

    public void update() throws IOException {
        if (!socket.isClosed()) {
            sendQueue.update();
        }

        receiveQueue.update();
    }

    public void sendMessage(Message m) {
        sendQueue.pushMessage(m, 0);
    }

    private void doNetworking() throws IOException {
        setupConnection();

        try {
            while (!Thread.currentThread().isInterrupted() && isValid()) {
                Message message = messageIO.readMessage();
                receiveQueue.pushMessage(message, id);
                onMessage(message);
            }
        } catch (EOFException e) {
            Log.info("Disconnect");
        } catch (IOException ioe) {
            errorInConnection();
            throw ioe;
        } finally {
            disposeConnection();
        }
    }

    protected abstract void setupConnection() throws IOException;

    protected abstract void disposeConnection() throws IOException;

    protected abstract void errorInConnection() throws IOException;

    protected void onMessage(Message m) throws IOException {
    }

    public boolean isValid() {
        return !socket.isClosed();
    }

    public void kill() throws IOException {
        // TODO: send close message
        // TODO: socket.shutdownOutput();
        socket.close();
    }

    private class SendMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            messageIO.sendMessage(message);
        }
    }
}
