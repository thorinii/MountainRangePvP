package mountainrangepvp.net.tcp

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption, EventLoopGroup}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.server.Server

/**
 * Exposes the server interface over TCP.
 */
object TcpServerWrapper {
  def start(server: Server, port: Int): TcpServerWrapper = {
    new TcpServerWrapper(server, port)
  }
}

class TcpServerWrapper(server: Server, port: Int) {
  private final val bossGroup: EventLoopGroup = new NioEventLoopGroup
  private final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  private var channel: ChannelFuture = null

  def start() {
    val b = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(classOf[NioServerSocketChannel])
            .childHandler(channelInitializer)
            .option(ChannelOption.SO_BACKLOG, 128.asInstanceOf[Integer])
            .childOption(ChannelOption.SO_KEEPALIVE, true.asInstanceOf[java.lang.Boolean])
    channel = b.bind(port).sync
  }

  private val channelInitializer = new ChannelInitializer[SocketChannel] {
    def initChannel(ch: SocketChannel) {
      ch.pipeline.addLast(new LengthFieldPrepender(4))

      ch.pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
      ch.pipeline.addLast(new ServerSideMessageHandler(server))
    }
  }

  def shutdown() {
    channel.channel.close
    channel.channel.closeFuture.syncUninterruptibly
    workerGroup.shutdownGracefully.syncUninterruptibly
    bossGroup.shutdownGracefully.syncUninterruptibly
  }
}
