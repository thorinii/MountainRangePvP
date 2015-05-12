package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.AttributeKey
import mountainrangepvp.engine.util.Log
import mountainrangepvp.game.world.ClientId
import mountainrangepvp.net._

/**
 * Decodes messages from the client
 */
class ServerSideMessageHandler(log: Log, server: ServerInterface) extends SimpleChannelInboundHandler[ByteBuf] {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    server.connect(new TcpClientInterface(ctx))
  }

  protected def channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) = {
    val m = MessageCodec.decode(buf)
    handle(idOf(ctx), m.asInstanceOf[ToServerMessage])
  }

  private def handle(client: ClientId, m: ToServerMessage) = server.receive(client, m)

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
    case e: Exception =>
      log.crash("Error in ServerSideMessageHandler", cause)
      super.exceptionCaught(ctx, cause)

    case _ =>
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    super.channelInactive(ctx)

    val client = idOf(ctx)
    if (client != null) {
      server.disconnect(client)
    }
  }

  private val idAttrKey = AttributeKey.valueOf[ClientId]("id")

  private def idOf(ctx: ChannelHandlerContext): ClientId = ctx.attr(idAttrKey).get

  private class TcpClientInterface(ctx: ChannelHandlerContext) extends ClientInterface {

    /**
     * This will never be called by the server.
     */
    override def disconnected() = {}


    /**
     * Forward a message over the network.
     */
    override def receive(message: ToClientMessage): Unit = message match {
      case ConnectedMessage(id) =>
        ctx.attr(idAttrKey).set(id)
        MessageCodec.send(ctx, message)

      case _ => MessageCodec.send(ctx, message)
    }
  }

}
