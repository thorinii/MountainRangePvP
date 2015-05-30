package mountainrangepvp.net.tcp

import java.io.IOException
import java.nio.channels.ClosedChannelException
import java.util.concurrent.TimeUnit

import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.util.concurrent.Future
import mountainrangepvp.core.ClientId
import mountainrangepvp.engine.Log
import mountainrangepvp.net._

class TcpServerInterface(log: Log, host: String, port: Int) extends ServerInterface {
  private final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  private var channel: ChannelFuture = null
  private var ctx: ChannelHandlerContext = null

  override def connect(client: ClientInterface) = {
    val b = new Bootstrap()
            .group(workerGroup)
            .channel(classOf[NioSocketChannel])
            .option(ChannelOption.SO_KEEPALIVE, true.asInstanceOf[java.lang.Boolean])
            .handler(channelInitializer(client))
    channel = b.connect(host, port).sync
  }

  /**
   * This method will never be called by the client; it's called by the receiving TCP side.
   */
  override def disconnect(client: ClientId) = {}

  override def shutdown() = {
    val futures: List[Future[_]] = List(
      channel.channel.close,
      workerGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS))
    futures.foreach(_.sync())
  }

  /**
   * Forward the message over the network.
   */
  override def deliver(clientId: ClientId, message: ToServerMessage) = try {
    MessageCodec.send(ctx, message)
  } catch {
    case _: ClosedChannelException =>
      // other things will close up the connection, so just log the error
      log.fine("Channel to " + clientId + " closed while sending")

    case e: IOException =>
      log.crash("Failed to deliver " + message + " to " + clientId, e)
  }


  private def channelInitializer(client: ClientInterface) = new ChannelInitializer[SocketChannel] {
    def initChannel(ch: SocketChannel) = {
      ch.pipeline.addLast("frameEncoder", new LengthFieldPrepender(4))

      ch.pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
      ch.pipeline.addLast("handler", new ClientSideMessageHandler(log, client))
      ch.pipeline.addLast("connection", new ConnectionListener)
    }
  }

  private class ConnectionListener extends ChannelInboundHandlerAdapter {
    @throws(classOf[Exception])
    override def channelActive(ctx: ChannelHandlerContext) = {
      TcpServerInterface.this.ctx = ctx
    }
  }

}
