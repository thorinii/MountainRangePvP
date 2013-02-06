/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import mountainrangepvp.mp.message.HelloMessage;
import mountainrangepvp.mp.message.Message;
import mountainrangepvp.mp.message.MessageIO;
import mountainrangepvp.mp.message.MessageQueue;

/**
 *
 * @author lachlan
 */
public class Client {

    private final String host;
    private final int port;
    private final MessageQueue messageQueue;
    private ServerProxy proxy;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;

        this.messageQueue = new MessageQueue();
    }

    public void start() throws IOException {
        Socket socket = new Socket(host, port);

        proxy = new ServerProxy(socket, messageQueue);
        new Thread(proxy).start();
    }

    public void stop() {
        try {
            proxy.kill();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void update() {
        messageQueue.update();
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    private class ServerProxy extends Proxy {

        public ServerProxy(Socket socket, MessageQueue messageQueue) throws
                IOException {
            super(socket, messageQueue);
        }

        @Override
        protected void setupConnection() throws IOException {
            messageIO.sendMessage(new HelloMessage());
            getHello();
        }

        private void getHello() throws IOException {
            Message m = messageIO.readMessage();

            if (m instanceof HelloMessage) {
                HelloMessage hello = (HelloMessage) m;

                if (!hello.isValid()) {
                    throw new IOException("Invalid Hello Message");
                }
            } else {
                throw new IOException("Invalid Message: " + m.getClass());
            }
        }

        @Override
        protected void disposeConnection() throws IOException {
        }
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", MultiplayerConstants.STD_PORT).start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
