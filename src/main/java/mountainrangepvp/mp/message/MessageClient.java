package mountainrangepvp.mp.message;

import mountainrangepvp.util.Log;
import mountainrangepvp.mp.MultiplayerConstants;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lachlan
 */
public class MessageClient {

    private final String host;
    private final int port;
    private final MessageQueue messageQueue;
    private ServerProxy proxy;

    public MessageClient(String host) {
        this(host, MultiplayerConstants.STD_PORT);
    }

    public MessageClient(String host, int port) {
        this.host = host;
        this.port = port;

        this.messageQueue = new MessageQueue();
    }

    public void start() throws IOException {
        Socket socket = new Socket(host, port);

        proxy = new ServerProxy(socket, messageQueue);
        new Thread(proxy, "Game Client").start();
    }

    public void stop() {
        try {
            proxy.kill();
        } catch (IOException ex) {
            Log.warn("Could not kill server connection:", ex);
        }
    }

    public void update() {
        try {
            proxy.update();
        } catch (IOException ex) {
            Log.warn("Error processing messages:", ex);
            stop();
        }
    }

    public boolean isConnected() {
        return proxy.isValid();
    }

    public void addMessageListener(MessageListener listener) {
        messageQueue.addListener(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageQueue.removeListener(listener);
    }

    @Deprecated
    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public void send(Message message) {
        proxy.sendMessage(message);
    }

    private class ServerProxy extends Proxy {

        public ServerProxy(Socket socket, MessageQueue messageQueue) throws
                IOException {
            super(socket, messageQueue);
        }

        @Override
        protected void setupConnection() throws IOException {
            messageIO.sendMessage(new ClientHelloMessage());
            getHello();
        }

        private void getHello() throws IOException {
            Message m = messageIO.readMessage();

            if (m instanceof ServerHelloMessage) {
                ServerHelloMessage hello = (ServerHelloMessage) m;

                if (!hello.isValid()) {
                    KillConnectionMessage kill = new KillConnectionMessage(
                            KillConnectionMessage.Reason.NetworkError);

                    messageIO.sendMessage(kill);
                    receiveQueue.pushMessage(kill);
                    throw new IOException("Invalid Hello Message");
                } else {
                    ready = true;

                    id = hello.getClientID();
                    receiveQueue.pushMessage(m, id);
                }
            } else {
                KillConnectionMessage kill = new KillConnectionMessage(
                        KillConnectionMessage.Reason.NetworkError);

                messageIO.sendMessage(kill);
                receiveQueue.pushMessage(kill);
                throw new IOException("Server killed in handshake");
            }

        }

        @Override
        protected void errorInConnection() throws IOException {
            receiveQueue.pushMessage(new KillConnectionMessage(
                    KillConnectionMessage.Reason.ServerShutdown), id);
        }

        @Override
        protected void disposeConnection() throws IOException {
        }

        @Override
        protected void onMessage(Message m) throws IOException {
            if (m instanceof KillConnectionMessage) {
                KillConnectionMessage kill = (KillConnectionMessage) m;

                Log.info("Server disconnected: " + kill.getReason());

                kill();
            }
        }
    }

    public static void main(String[] args) {
        try {
            new MessageClient("localhost", MultiplayerConstants.STD_PORT).
                    start();
        } catch (IOException ex) {
            Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE,
                                                                null, ex);
        }
    }
}
