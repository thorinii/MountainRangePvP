package mountainrangepvp.net.tcp

import java.nio.charset.StandardCharsets

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.ClientId
import mountainrangepvp.net.server.PlayerStats

/**
 * De/encodes messages into Netty {@link ByteBuf}s
 */
object MessageCodec {

  def send(ctx: ChannelHandlerContext, msg: Message): Unit = {
    ctx.writeAndFlush(encode(ctx, msg)).sync
  }

  def encode(ctx: ChannelHandlerContext, msg: Message): ByteBuf = {
    val buf = ctx.alloc().ioBuffer()
    encode(msg, buf)
    buf
  }

  private def encode(msg: Message, buf: ByteBuf): Unit = msg match {
    case ConnectedMessage(id) =>
      buf.writeInt(1)
      buf.writeLong(id.id)

    case LoginMessage(checkCode, version, nickname) =>
      buf.writeInt(2)
      buf.writeInt(checkCode)
      buf.writeInt(version)
      writeString(buf, nickname)

    case SessionInfoMessage(teamsOn) =>
      buf.writeInt(3)
      buf.writeBoolean(teamsOn)

    case NewMapMessage(seed) =>
      buf.writeInt(4)
      buf.writeInt(seed)

    case PlayerStatsMessage(stats) =>
      buf.writeInt(5)

      val players = stats.players
      buf.writeInt(players.size)

      players.foreach { case (id, nickname) =>
        buf.writeLong(id.id)
        writeString(buf, nickname)
      }
  }

  def decode(buf: ByteBuf): Message = {
    val `type` = buf.readInt()
    `type` match {
      case 1 =>
        ConnectedMessage(new ClientId(buf.readLong()))

      case 2 =>
        LoginMessage(buf.readInt(),
                     buf.readInt(),
                     readString(buf))

      case 3 =>
        SessionInfoMessage(buf.readBoolean())

      case 4 =>
        NewMapMessage(buf.readInt())

      case 5 =>
        val playerCount = buf.readInt()
        var players = Map.empty[ClientId, String]

        for (_ <- 1 to playerCount) {
          val id = buf.readLong()
          val nickname = readString(buf)
          players += (new ClientId(id) -> nickname)
        }

        PlayerStatsMessage(new PlayerStats(players))

      case _ =>
        Log.todoCrash()
        throw new UnsupportedOperationException
    }
  }

  private def writeString(buf: ByteBuf, string: String): Unit = {
    val bytes = string.getBytes(StandardCharsets.UTF_8)
    buf.writeInt(bytes.length)
    buf.writeBytes(bytes)
  }

  private def readString(buf: ByteBuf): String = {
    val length = buf.readInt()
    val bytes = new Array[Byte](length)
    buf.readBytes(bytes, 0, length)
    new String(bytes)
  }
}
