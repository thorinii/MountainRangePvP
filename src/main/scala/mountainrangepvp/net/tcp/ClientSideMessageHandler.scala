package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.ClientInterface

/**
 * Decodes messages from the server.
 */
class ClientSideMessageHandler(client: ClientInterface) extends SimpleChannelInboundHandler[ByteBuf]() {

  protected def channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) {
    val m = MessageCodec.decode(buf)
    handle(m)
  }

  private def handle(m: Message) = m match {
    case ConnectedMessage(id) => client.connected(id)
  }


  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    Log.info("Disconnected from server")
    super.channelInactive(ctx)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
    case e: Exception =>
      Log.crash("Error in ClientSideMessageHandler", cause)
      super.exceptionCaught(ctx, cause)

    case _ =>
  }
}
