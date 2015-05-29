package mountainrangepvp.net.tcp

import java.util.concurrent.TimeUnit

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption, EventLoopGroup}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.util.concurrent.Future
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.server.LocalServerInterface

class TcpServerWrapper(log: Log, server: LocalServerInterface, port: Int) {
  private final val bossGroup: EventLoopGroup = new NioEventLoopGroup
  private final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  private var channel: ChannelFuture = null

  def start() = {
    val b = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(classOf[NioServerSocketChannel])
            .childHandler(channelInitializer)
            .option(ChannelOption.SO_BACKLOG, 128.asInstanceOf[Integer])
            .childOption(ChannelOption.SO_KEEPALIVE, true.asInstanceOf[java.lang.Boolean])
    channel = b.bind(port).sync
  }

  private val channelInitializer = new ChannelInitializer[SocketChannel] {
    def initChannel(ch: SocketChannel) = {
      ch.pipeline.addLast(new LengthFieldPrepender(4))

      ch.pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
      ch.pipeline.addLast(new ServerSideMessageHandler(log, server))
    }
  }

  def shutdown() = {
    val futures: List[Future[_]] = List(
      channel.channel.close,
      workerGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS),
      bossGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS))
    futures.foreach(_.sync())
  }
}
