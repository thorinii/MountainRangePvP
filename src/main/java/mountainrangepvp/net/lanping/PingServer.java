package mountainrangepvp.net.lanping;

import mountainrangepvp.engine.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author lachlan
 */
public class PingServer {

    private final Log log;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private Thread pingThread;

    public PingServer(Log log) {
        this.log = log;
    }

    public void start() throws IOException {
        socket = new DatagramSocket();
        packet = makePacket();

        pingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        ping();
                        Thread.sleep(1000 / PingConstants.PING_RATE);
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
        byte[] data = PingConstants.PING_DATA;
        return new DatagramPacket(data, data.length,
                                  PingConstants.MULTICAST_ADDRESS,
                                  PingConstants.MULTICAST_PORT);
    }

    private void ping() {
        try {
            socket.send(packet);
        } catch (IOException ioe) {
            log.warn("Could not send ping", ioe);
            Thread.currentThread().interrupt();
        }
    }
}
