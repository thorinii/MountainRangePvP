package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.ClientInterface

/**
 * Decodes messages from the server.
 */
class ClientSideMessageHandler(client: ClientInterface) extends SimpleChannelInboundHandler[ByteBuf]() {
  @throws(classOf[Exception])
  protected def channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) {
    val m = Message.decode(buf)
    Log.info("Receiving " + m)
    handle(m)
  }

  private def handle(m: Message) = m match {
    case ConnectedMessage(id) => client.connected(id)
  }


  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    Log.info("Disconnected from server")
    super.channelInactive(ctx)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    Log.crash("Error in ClientSideMessageHandler", cause)
    super.exceptionCaught(ctx, cause)
  }
}