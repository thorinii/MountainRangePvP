package mountainrangepvp.net.tcp;

import mountainrangepvp.net.server.Server;

/**
 * Exposes the server interface over TCP.
 */
public class TcpWrapper {
    public static TcpWrapper start(Server server) {
        return new TcpWrapper();
    }

    private TcpWrapper() {
    }

    public void kill() {

    }
}
