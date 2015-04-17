package mountainrangepvp.net.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.net.ClientId;
import mountainrangepvp.net.ClientInterface;
import mountainrangepvp.net.ServerInterface;

/**
 * Delegates the server interface to a remote server via TCP.
 */
public class TcpServerInterface implements ServerInterface {
    public static TcpServerInterface start(String serverIp, int port) {
        return new TcpServerInterface(serverIp, port);
    }

    private final EventLoopGroup workerGroup;
    private final String serverIp;
    private final int port;

    private TcpServerInterface(String serverIp, int port) {
        this.workerGroup = new NioEventLoopGroup();
        this.serverIp = serverIp;
        this.port = port;
    }

    @Override
    public void connect(ClientInterface client) {
        Log.todo();
    }

    @Override
    public void login(ClientId client, int checkCode, int version, String nickname) {
        Log.todo();
    }

    @Override
    public void shutdown() {
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }
}
