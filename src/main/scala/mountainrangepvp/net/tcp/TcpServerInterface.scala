package mountainrangepvp.net.tcp

import java.util.concurrent.TimeUnit

import com.badlogic.gdx.math.Vector2
import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.util.concurrent.Future
import mountainrangepvp.game.world.ClientId
import mountainrangepvp.net.{ClientInterface, ServerInterface}

class TcpServerInterface(host: String, port: Int) extends ServerInterface {
  private final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  private var channel: ChannelFuture = null
  private var ctx: ChannelHandlerContext = null

  def connect(client: ClientInterface) = {
    val b = new Bootstrap()
            .group(workerGroup)
            .channel(classOf[NioSocketChannel])
            .option(ChannelOption.SO_KEEPALIVE, true.asInstanceOf[java.lang.Boolean])
            .handler(channelInitializer(client))
    channel = b.connect(host, port).sync
  }

  private def channelInitializer(client: ClientInterface) = new ChannelInitializer[SocketChannel] {
    def initChannel(ch: SocketChannel) = {
      ch.pipeline.addLast("frameEncoder", new LengthFieldPrepender(4))

      ch.pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
      ch.pipeline.addLast("handler", new ClientSideMessageHandler(client))
      ch.pipeline.addLast("connection", new ConnectionListener)
    }
  }

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    send(new LoginMessage(checkCode, version, nickname))
  }

  def shutdown() = {
    val futures: List[Future[_]] = List(
      channel.channel.close,
      workerGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS))
    futures.foreach(_.sync())
  }

  def fireShot(client: ClientId, direction: Vector2) = {
    send(new FireShotMessage(direction))
  }

  private def send(msg: Message) = {
    MessageCodec.send(ctx, msg)
  }

  private class ConnectionListener extends ChannelInboundHandlerAdapter {
    @throws(classOf[Exception])
    override def channelActive(ctx: ChannelHandlerContext) = {
      TcpServerInterface.this.ctx = ctx
    }
  }

}
