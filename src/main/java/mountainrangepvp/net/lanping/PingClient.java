package mountainrangepvp.net.lanping;

import mountainrangepvp.engine.util.Log;
import mountainrangepvp.game.mp.MultiplayerConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.*;

/**
 * @author lachlan
 */
public class PingClient {

    private final Log log;
    private final Set<ServerData> servers;
    private MulticastSocket socket;
    private DatagramPacket packet;
    private Thread listenThread;

    public PingClient(Log log) {
        this.log = log;
        servers = new HashSet<>();
    }

    public void start() throws IOException {
        socket = new MulticastSocket(MultiplayerConstants.MULTICAST_PORT);
        socket.joinGroup(MultiplayerConstants.MULTICAST_ADDRESS);

        packet = makePacket();

        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        read();
                        process();

                        Thread.sleep(1000 / MultiplayerConstants.PING_RATE);
                    }
                } catch (InterruptedException ex) {
                    log.fine("Ping Client shutting down");
                }
            }
        }, "Ping Client");
        listenThread.setDaemon(true);
        listenThread.start();
    }

    public void stop() {
        listenThread.interrupt();
        socket.close();
    }

    private DatagramPacket makePacket() {
        byte[] data = new byte[MultiplayerConstants.PING_DATA.length];
        return new DatagramPacket(data, data.length);
    }

    private void read() {
        try {
            socket.receive(packet);
        } catch (IOException ioe) {
            if (!socket.isClosed())
                log.warn("Could not read ping", ioe);
            Thread.currentThread().interrupt();
        }
    }

    private void process() {
        byte[] data = packet.getData();

        if (Arrays.equals(data, MultiplayerConstants.PING_DATA)) {
            ServerData server = new ServerData(packet.getAddress().
                    getHostAddress());

            servers.remove(server);
            servers.add(server);
        }

        packet.setLength(data.length);
    }

    public List<ServerData> getServers() {
        List<ServerData> tmp = new ArrayList<>(servers);
        List<ServerData> list = new ArrayList<>();

        for (ServerData server : tmp) {
            if (server.getFreshness() < MultiplayerConstants.PING_SERVER_FRESHNESS) {
                list.add(server);
            }
        }

        return list;
    }

    public static class ServerData {

        public final String ip;
        public final long foundTime;

        public ServerData(String ip) {
            this.ip = ip;
            this.foundTime = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ServerData other = (ServerData) obj;
            if (!Objects.equals(this.ip, other.ip)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return ip.hashCode();
        }

        public int getFreshness() {
            return (int) (System.currentTimeMillis() - foundTime);
        }

        @Override
        public String toString() {
            return "Server[" + "ip=" + ip + ", freshness=" + getFreshness() + "ms]";
        }
    }
}
