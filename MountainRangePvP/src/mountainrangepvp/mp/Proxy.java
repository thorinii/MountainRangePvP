/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import mountainrangepvp.Log;
import mountainrangepvp.mp.message.*;

/**
 *
 * @author lachlan
 */
public abstract class Proxy implements Runnable {

    protected final Socket socket;
    protected final MessageIO messageIO;
    protected final MessageQueue messageQueue;

    public Proxy(Socket socket, MessageQueue messageQueue) throws IOException {
        this.socket = socket;
        this.messageIO = new MessageIO(socket);
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            doNetworking();
        } catch (IOException ioe) {
            Log.warn("Error reading messages:", ioe);
        }
    }

    private void doNetworking() throws IOException {
        setupConnection();

        try {
            while (!Thread.currentThread().isInterrupted() && isValid()) {
                Message message = messageIO.readMessage();
                messageQueue.pushMessage(message, this);
                onMessage(message);
            }
        } catch (EOFException eofe) {
            Log.info("Abnormal disconnect:", eofe);
        }

        disposeConnection();
    }

    protected abstract void setupConnection() throws IOException;

    protected abstract void disposeConnection() throws IOException;

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
}
