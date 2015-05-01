package mountainrangepvp.net.tcp

import java.nio.charset.StandardCharsets

import com.badlogic.gdx.math.Vector2
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mountainrangepvp.game.world.{ClientId, PlayerStats}

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

    case FireShotMessage(direction) =>
      buf.writeInt(6)
      writeVector(buf, direction)

    case PlayerFiredMessage(client, from, direction) =>
      buf.writeInt(7)
      buf.writeLong(client.id)
      writeVector(buf, from)
      writeVector(buf, direction)
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

      case 6 =>
        FireShotMessage(readVector(buf))

      case 7 =>
        PlayerFiredMessage(ClientId(buf.readLong()),
                           readVector(buf),
                           readVector(buf))

      case _ =>
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

  private def writeVector(buf: ByteBuf, v: Vector2): Unit = {
    buf.writeFloat(v.x).writeFloat(v.y)
  }

  private def readVector(buf: ByteBuf): Vector2 = {
    val x = buf.readFloat()
    val y = buf.readFloat()
    new Vector2(x, y)
  }
}
