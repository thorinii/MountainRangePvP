/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public Server(int seed) {
        this(MultiplayerConstants.STD_PORT, seed);
    }

    public Server(int port, int seed) {
        this.port = port;
        this.seed = seed;
        clients = new LinkedList<>();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);

        acceptThread = new Thread(new AcceptRunnable(serverSocket));
        acceptThread.start();
    }

    public void update() {
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

                    ClientProxy crr = new ClientProxy(socket, seed);
                    clients.add(crr);
                    new Thread(crr).start();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private class ClientProxy implements Runnable {

        private final Socket socket;
        private final int seed;
        private final DataOutputStream dos;
        private final DataInputStream dis;

        public ClientProxy(Socket socket, int seed) throws IOException {
            this.socket = socket;
            this.seed = seed;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
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
            System.out.println("sending hello");
            sendHello();
            System.out.println("waiting hello");
            getHello();
            System.out.println("get hello");

            sendSeed();

            while (!Thread.currentThread().isInterrupted()) {
                dis.readByte();
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

        private void sendSeed() throws IOException {
            dos.writeInt(MultiplayerConstants.MESSAGE_SEED);
            dos.writeInt(seed);
            dos.flush();
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
            new Server(21342).start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
