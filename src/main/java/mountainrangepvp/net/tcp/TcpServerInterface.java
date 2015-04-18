package mountainrangepvp.net.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.net.ClientId;
import mountainrangepvp.net.ClientInterface;
import mountainrangepvp.net.ServerInterface;

/**
 * Delegates the server interface to a remote server via TCP.
 */
public class TcpServerInterface implements ServerInterface {
    public static TcpServerInterface start(String host, int port) {
        return new TcpServerInterface(host, port);
    }

    private final EventLoopGroup workerGroup;
    private final String host;
    private final int port;
    private ChannelFuture channel;
    private ChannelHandlerContext ctx;

    private TcpServerInterface(String host, int port) {
        this.workerGroup = new NioEventLoopGroup();
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect(final ClientInterface client) throws InterruptedException {
        Bootstrap b = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));

                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                        ch.pipeline().addLast("handler", new ClientSideMessageHandler(client));
                        ch.pipeline().addLast("connection", new ConnectionListener());
                    }
                });

        channel = b.connect(host, port).sync();
    }

    @Override
    public void login(ClientId client, int checkCode, int version, String nickname) {
        Log.todo();
        send(new LoginMessage(checkCode, version, nickname));
    }

    @Override
    public void shutdown() {
        channel.channel().close();
        channel.channel().closeFuture().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    private void send(Message msg) {
        Message$.MODULE$.send(ctx, msg);
    }

    private class ConnectionListener extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            TcpServerInterface.this.ctx = ctx;
        }
    }
}
