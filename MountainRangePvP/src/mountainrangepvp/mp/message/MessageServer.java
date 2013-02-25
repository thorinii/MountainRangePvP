/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import mountainrangepvp.Log;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.mp.lanping.PingServer;

/**
 *
 * @author lachlan
 */
public class MessageServer {

    private final int port;
    //
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private PingServer pingServer;
    //
    private final List<ClientProxy> clients;
    //
    private final MessageQueue messageQueue;
    //
    private int currentID;

    public MessageServer() {
        this(MultiplayerConstants.STD_PORT);
    }

    public MessageServer(int port) {
        this.port = port;
        clients = new LinkedList<>();

        messageQueue = new MessageQueue();

        currentID = 2;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);

        acceptThread = new Thread(new AcceptRunnable(serverSocket),
                                  "Game Server");
        acceptThread.start();

        pingServer = new PingServer();
        pingServer.start();
    }

    public void stop() {
        try {
            acceptThread.interrupt();
            serverSocket.close();

            pingServer.stop();
        } catch (IOException ioe) {
            Log.warn("Error stopping server", ioe);
        }

        for (ClientProxy client : clients) {
            try {
                client.kill();
            } catch (IOException ioe) {
                Log.warn("Error stopping client connection", ioe);
            }
        }

        clients.clear();
    }

    public void addMessageListener(MessageListener messageListener) {
        messageQueue.addListener(messageListener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageQueue.removeListener(listener);
    }

    public void send(Message message, int id) {
        for (ClientProxy proxy : clients) {
            if (proxy.id == id) {
                proxy.sendMessage(message);
            }
        }
    }

    public void broadcast(Message message) {
        for (ClientProxy proxy : clients) {
            proxy.sendMessage(message);
        }
    }

    public void broadcastExcept(Message message, int notID) {
        for (ClientProxy proxy : clients) {
            if (proxy.id != notID) {
                proxy.sendMessage(message);
            }
        }
    }

    public void update() {
        if (serverSocket.isClosed()) {
            return;
        }

        List<ClientProxy> tmp = new ArrayList<>(clients);

        for (ClientProxy proxy : tmp) {
            try {
                proxy.update();
            } catch (IOException ex) {
                Log.warn("Error processing client:", ex);

                try {
                    proxy.kill();
                } catch (IOException ioe) {
                    Log.warn("Error killing client connection", ioe);
                }
            }
        }
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public List<Integer> getConnectedClients() {
        List<Integer> ids = new ArrayList<>();

        for (ClientProxy proxy : clients)
            if (proxy.isValid())
                ids.add(proxy.id);

        return ids;
    }

    public boolean isGoing() {
        return !serverSocket.isClosed();
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
                Log.warn("Error accepting clients:", ioe);
            }
        }
    }

    public class ClientProxy extends Proxy {

        public ClientProxy(Socket socket, MessageQueue messageQueue) throws
                IOException {
            super(currentID++, socket, messageQueue);
        }

        @Override
        protected void setupConnection() throws IOException {
            getHello();
            messageIO.sendMessage(new ServerHelloMessage(id));
        }

        private void getHello() throws IOException {
            Message m = messageIO.readMessage();

            if (m instanceof ClientHelloMessage) {
                ClientHelloMessage hello = (ClientHelloMessage) m;

                if (!hello.isValid()) {
                    KillConnectionMessage kill = new KillConnectionMessage(
                            KillConnectionMessage.Reason.NetworkError);

                    messageIO.sendMessage(kill);
                    receiveQueue.pushMessage(kill);
                    throw new IOException("Invalid Hello Message");
                } else {
                    ready = true;
                    receiveQueue.pushMessage(m);
                }
            } else {
                KillConnectionMessage kill = new KillConnectionMessage(
                        KillConnectionMessage.Reason.NetworkError);

                messageIO.sendMessage(kill);
                receiveQueue.pushMessage(kill);
                throw new IOException("Invalid Hello Message");
            }
        }

        @Override
        protected void errorInConnection() throws IOException {
        }

        @Override
        protected void disposeConnection() throws IOException {
            receiveQueue.pushMessage(new KillConnectionMessage(
                    KillConnectionMessage.Reason.ClientExit), id);
            clients.remove(this);
        }

        @Override
        protected void onMessage(Message m) throws IOException {
            if (m instanceof KillConnectionMessage) {
                KillConnectionMessage kill = (KillConnectionMessage) m;
                Log.info(id + " disconnected: " + kill.getReason());

                kill();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MessageServer server = new MessageServer();
        server.messageQueue.setInQueueMode(false);
        server.messageQueue.addListener(new MessageListener() {

            @Override
            public void accept(Message message, int id) throws
                    IOException {
                System.out.println(id + " sent " + message);
            }
        });
        server.start();

        System.out.println("Test Server started");
    }
}
