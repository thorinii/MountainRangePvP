package mountainrangepvp.net.tcp

import com.badlogic.gdx.math.Vector2
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.AttributeKey
import mountainrangepvp.engine.util.Log
import mountainrangepvp.game.world.{ClientId, PlayerStats}
import mountainrangepvp.net.{ClientInterface, ServerInterface}

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

  private def handle(client: ClientId, m: ToServerMessage) = m match {
    case LoginMessage(c, v, n) => server.login(client, c, v, n)
    case FireShotMessage(d) => server.fireShot(client, d)
    case _ => log.todo(m.toString)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
    case e: Exception =>
      log.crash("Error in ServerSideMessageHandler", cause)
      super.exceptionCaught(ctx, cause)

    case _ =>
  }


  private val idAttrKey = AttributeKey.valueOf[ClientId]("id")

  private def idOf(ctx: ChannelHandlerContext): ClientId = ctx.attr(idAttrKey).get

  private class TcpClientInterface(ctx: ChannelHandlerContext) extends ClientInterface {
    override def connected(id: ClientId) = {
      ctx.attr(idAttrKey).set(id)

      MessageCodec.send(ctx, ConnectedMessage(id))
    }

    override def sessionInfo(teamsOn: Boolean) = {
      MessageCodec.send(ctx, SessionInfoMessage(teamsOn))
    }

    override def newMap(seed: Int) = {
      MessageCodec.send(ctx, NewMapMessage(seed))
    }

    override def playerStats(stats: PlayerStats) = {
      MessageCodec.send(ctx, PlayerStatsMessage(stats))
    }

    override def firedShot(client: ClientId, from: Vector2, direction: Vector2) = {
      MessageCodec.send(ctx, PlayerFiredMessage(client, from, direction))
    }
  }

}
