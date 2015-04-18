package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.{ClientId, ClientInterface, ServerInterface}

/**
 * Decodes messages from the client
 */
class ServerSideMessageHandler(server: ServerInterface) extends SimpleChannelInboundHandler[ByteBuf] {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    server.connect(new TcpClientInterface(ctx))
  }

  @throws(classOf[Exception])
  protected def channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) {
    val m = Message.decode(buf)
    handle(m)
  }

  private def handle(m: Message) = m match {
    case _ => Log.todo(m.toString)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    Log.crash("Error in ServerSideMessageHandler", cause)
    super.exceptionCaught(ctx, cause)
  }

  private class TcpClientInterface(ctx: ChannelHandlerContext) extends ClientInterface {
    override def connected(id: ClientId): Unit = {
      Message.send(ctx, ConnectedMessage(id))
    }
  }
}
