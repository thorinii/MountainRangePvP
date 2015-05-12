package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net._

/**
 * Decodes messages from the server.
 */
class ClientSideMessageHandler(log: Log, client: ClientInterface) extends SimpleChannelInboundHandler[ByteBuf]() {

  protected def channelRead0(ctx: ChannelHandlerContext, buf: ByteBuf) = {
    val m = MessageCodec.decode(buf)
    handle(m.asInstanceOf[ToClientMessage])
  }

  private def handle(m: ToClientMessage) = m match {
    case ConnectedMessage(id) => client.connected(id)
    case SessionInfoMessage(teamsOn) => client.sessionInfo(teamsOn)
    case NewMapMessage(seed) => client.newMap(seed)
    case PlayerStatsMessage(stats) => client.playerStats(stats)
    case PlayerFiredMessage(id, from, direction) => client.firedShot(id, from, direction)
    case PingMessage(id) => client.ping(id)
    case PingedMessage(lag) => client.pinged(lag)
  }


  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    super.channelInactive(ctx)

    client.disconnected()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
    case e: Exception =>
      log.crash("Error in ClientSideMessageHandler", cause)
      super.exceptionCaught(ctx, cause)

    case _ =>
  }
}
