package mountainrangepvp.net.lanping;

import mountainrangepvp.game.mp.MultiplayerConstants;
import mountainrangepvp.engine.util.LegacyLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author lachlan
 */
public class PingServer {

    private DatagramSocket socket;
    private DatagramPacket packet;
    private Thread pingThread;

    public void start() throws IOException {
        socket = new DatagramSocket();
        packet = makePacket();

        pingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        ping();
                        Thread.sleep(1000 / MultiplayerConstants.PING_RATE);
                    }
                } catch (InterruptedException ex) {
                    // shut down
                }
            }
        }, "Ping Server");
        pingThread.setDaemon(true);
        pingThread.start();
    }

    public void stop() {
        pingThread.interrupt();
        socket.close();
    }

    private DatagramPacket makePacket() {
        byte[] data = MultiplayerConstants.PING_DATA;
        return new DatagramPacket(data, data.length,
                                  MultiplayerConstants.MULTICAST_ADDRESS,
                                  MultiplayerConstants.MULTICAST_PORT);
    }

    private void ping() {
        try {
            socket.send(packet);
        } catch (IOException ioe) {
            LegacyLog.warn("Could not send ping", ioe);
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        PingServer server = new PingServer();
        server.start();

        while (true) {
            Thread.sleep(1000);
        }
    }
}
