package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.AttributeKey
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.{ClientId, ClientInterface, ServerInterface}

/**
 * Decodes messages from the client
 */
class ServerSideMessageHandler(server: ServerInterface) extends SimpleChannelInboundHandler[ByteBuf] {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    server.connect(new TcpClientInterface(ctx))
  }

  protected def channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) = {
    val m = MessageCodec.decode(buf)
    handle(idOf(ctx), m.asInstanceOf[ToServerMessage])
  }

  private def handle(client: ClientId, m: ToServerMessage) = m match {
    case LoginMessage(c, v, n) => server.login(client, c, v, n)
    case _ => Log.todo(m.toString)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
    case e: Exception =>
      Log.crash("Error in ServerSideMessageHandler", cause)
      super.exceptionCaught(ctx, cause)

    case _ =>
  }


  private val idAttrKey = AttributeKey.valueOf[ClientId]("id")

  private def idOf(ctx: ChannelHandlerContext): ClientId = ctx.attr(idAttrKey).get

  private class TcpClientInterface(ctx: ChannelHandlerContext) extends ClientInterface {
    override def connected(id: ClientId): Unit = {
      ctx.attr(idAttrKey).set(id)

      MessageCodec.send(ctx, ConnectedMessage(id))
    }

    override def instanceInfo(teamsOn: Boolean): Unit = {
      MessageCodec.send(ctx, InstanceInfoMessage(teamsOn))
    }
  }
}
