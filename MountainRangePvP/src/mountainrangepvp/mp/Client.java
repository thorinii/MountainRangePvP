/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lachlan
 */
public class Client {

    private final String host;
    private final int port;
    private ServerProxy proxy;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        Socket socket = new Socket(host, port);

        proxy = new ServerProxy(socket);
        new Thread(proxy).start();
    }

    public int getSeed() {
        try {
            proxy.connectedLatch.await();
            return proxy.seed;
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            return -1;
        }
    }

    private class ServerProxy implements Runnable {

        final CountDownLatch connectedLatch;
        final Socket socket;
        final DataOutputStream dos;
        final DataInputStream dis;
        int seed;

        public ServerProxy(Socket socket) throws IOException {
            this.socket = socket;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

            connectedLatch = new CountDownLatch(1);
        }

        @Override
        public void run() {
            try {
                doNetworking();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        private void doNetworking() throws IOException {
            System.out.println("wait hello");
            getHello();
            System.out.println("get/sending hello");
            sendHello();
            System.out.println("sent hello");

            connectedLatch.countDown();
            while (!Thread.currentThread().isInterrupted()) {
                int message = dis.readInt();

                if (message == MultiplayerConstants.MESSAGE_SEED) {
                    seed = dis.readInt();
                }
            }
        }

        private void sendHello() throws IOException {
            dos.writeInt(MultiplayerConstants.CHECK_CODE);
            dos.writeInt(MultiplayerConstants.VERSION);
            dos.writeInt(MultiplayerConstants.MESSAGE_HELLO);
            dos.flush();
        }

        private void getHello() throws IOException {
            int checkCode = dis.readInt();
            if (checkCode != MultiplayerConstants.CHECK_CODE) {
                throw new IOException("Invalid Check Code");
            }

            int version = dis.readInt();
            if (version != MultiplayerConstants.VERSION) {
                throw new IOException("Incompatible protocol version");
            }

            int message = dis.readInt();
            if (message != MultiplayerConstants.MESSAGE_HELLO) {
                throw new IOException("Expected Hello Message");
            }
        }

        public boolean isValid() {
            return !socket.isClosed();
        }

        public void kill() throws IOException {
            // TODO: send close message
            // TODO: kill socket
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
