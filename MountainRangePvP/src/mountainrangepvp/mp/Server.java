/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mountainrangepvp.mp.message.*;

/**
 *
 * @author lachlan
 */
public class Server {

    private final int port;
    private final int seed;
    //
    private ServerSocket serverSocket;
    private Thread acceptThread;
    //
    private final List<ClientProxy> clients;
    //
    private final MessageQueue messageQueue;

    public Server(int seed) {
        this(MultiplayerConstants.STD_PORT, seed);
    }

    public Server(int port, int seed) {
        this.port = port;
        this.seed = seed;
        clients = new LinkedList<>();

        this.messageQueue = new MessageQueue();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);

        acceptThread = new Thread(new AcceptRunnable(serverSocket));
        acceptThread.start();
    }

    public void stop() {
        try {
            acceptThread.interrupt();
            serverSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        for (ClientProxy client : clients) {
            try {
                client.kill();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void update() {
        messageQueue.update();
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    private class AcceptRunnable implements Runnable {

        private final ServerSocket serverSocket;

        public AcceptRunnable(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();

                    ClientProxy crr = new ClientProxy(socket, messageQueue);
                    clients.add(crr);
                    new Thread(crr).start();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private class ClientProxy extends Proxy {

        public ClientProxy(Socket socket, MessageQueue messageQueue) throws
                IOException {
            super(socket, messageQueue);
        }

        @Override
        protected void setupConnection() throws IOException {
            messageIO.sendMessage(new HelloMessage());
            getHello();

            messageIO.sendMessage(new SeedMessage(seed));
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
            new Server(21342).start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
