package mountainrangepvp.net.tcp;

import mountainrangepvp.engine.util.Log;
import mountainrangepvp.net.ServerInterface;

/**
 * Delegates the server interface to a remote server via TCP.
 */
public class TcpServerInterface implements ServerInterface {
    public static TcpServerInterface start(String serverIP, int port) {
        return new TcpServerInterface();
    }

    @Override
    public void connect(int checkCode, int version, String nickname) {
        Log.todo();
    }
}
