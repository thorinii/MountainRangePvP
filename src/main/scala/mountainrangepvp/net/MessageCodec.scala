package mountainrangepvp.net

import java.nio.charset.StandardCharsets
import java.time.Duration

import com.badlogic.gdx.math.Vector2
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mountainrangepvp.game.world.{Shot, ClientId, Player, Snapshot}

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
      writeId(buf, id)

    case LoginMessage(checkCode, version, nickname) =>
      buf.writeInt(2)
      buf.writeInt(checkCode)
      buf.writeInt(version)
      writeString(buf, nickname)

    case SessionInfoMessage(teamsOn) =>
      buf.writeInt(3)
      buf.writeBoolean(teamsOn)

    case SnapshotMessage(snapshot) =>
      buf.writeInt(4)
      writeSnapshot(buf, snapshot)

    case FireShotMessage(direction) =>
      buf.writeInt(6)
      writeVector(buf, direction)

    case PingMessage(id) =>
      buf.writeInt(8)
      buf.writeInt(id)

    case PongMessage(id) =>
      buf.writeInt(9)
      buf.writeInt(id)

    case PingedMessage(lag) =>
      buf.writeInt(10)
      buf.writeLong(lag.toMillis)
  }

  def decode(buf: ByteBuf): Message = {
    val `type` = buf.readInt()
    `type` match {
      case 1 =>
        ConnectedMessage(readId(buf))

      case 2 =>
        LoginMessage(buf.readInt(),
                     buf.readInt(),
                     readString(buf))

      case 3 =>
        SessionInfoMessage(buf.readBoolean())

      case 4 =>
        SnapshotMessage(readSnapshot(buf))

      case 6 =>
        FireShotMessage(readVector(buf))

      case 8 =>
        PingMessage(buf.readInt())

      case 9 =>
        PongMessage(buf.readInt())

      case 10 =>
        PingedMessage(Duration.ofMillis(buf.readLong()))

      case _ =>
        throw new UnsupportedOperationException
    }
  }

  private def writeSnapshot(buf: ByteBuf, snapshot: Snapshot) = {
    buf.writeInt(snapshot.seed)
    buf.writeBoolean(snapshot.teamsOn)
    buf.writeInt(snapshot.players.size)
    for (p <- snapshot.players) writePlayer(buf, p)
    buf.writeInt(snapshot.shots.size)
    for(s <- snapshot.shots) writeShot(buf, s)
  }

  private def readSnapshot(buf: ByteBuf) = {
    Snapshot(buf.readInt(), buf.readBoolean(),
             0.until(buf.readInt()).map(_ => readPlayer(buf)).toSet,
             0.until(buf.readInt()).map(_ => readShot(buf)).toSet)
  }

  private def writePlayer(buf: ByteBuf, player: Player) = {
    writeId(buf, player.id)
    writeString(buf, player.nickname)
  }

  private def readPlayer(buf: ByteBuf) = Player(readId(buf), readString(buf))


  private def writeShot(buf: ByteBuf, shot: Shot) = {
    writeId(buf, ClientId.Invalid)
    writeVector(buf, shot.base)
    writeVector(buf, shot.direction)
  }

  private def readShot(buf: ByteBuf) = new Shot(readId(buf), readVector(buf), readVector(buf))


  private def writeId(buf: ByteBuf, id: ClientId) = {
    buf.writeLong(id.id)
  }

  private def readId(buf: ByteBuf) = ClientId(buf.readLong())

  private def writeString(buf: ByteBuf, string: String) = {
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

  private def writeVector(buf: ByteBuf, v: Vector2) = {
    buf.writeFloat(v.x).writeFloat(v.y)
  }

  private def readVector(buf: ByteBuf): Vector2 = {
    val x = buf.readFloat()
    val y = buf.readFloat()
    new Vector2(x, y)
  }
}
