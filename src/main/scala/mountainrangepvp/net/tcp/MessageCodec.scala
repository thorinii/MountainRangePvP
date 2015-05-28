package mountainrangepvp.net.tcp

import java.nio.charset.StandardCharsets
import java.time.Duration

import com.badlogic.gdx.math.Vector2
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mountainrangepvp.game.world._
import mountainrangepvp.net._

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

    case SnapshotMessage(snapshot) =>
      buf.writeInt(4)
      writeSnapshot(buf, snapshot)

    case CommandMessage(command) =>
      buf.writeInt(6)
      writeInputCommand(buf, command)

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
    val message = `type` match {
      case 1 =>
        ConnectedMessage(readId(buf))

      case 2 =>
        LoginMessage(buf.readInt(),
                     buf.readInt(),
                     readString(buf))

      case 4 =>
        SnapshotMessage(readSnapshot(buf))

      case 6 =>
        CommandMessage(readInputCommand(buf))

      case 8 =>
        PingMessage(buf.readInt())

      case 9 =>
        PongMessage(buf.readInt())

      case 10 =>
        PingedMessage(Duration.ofMillis(buf.readLong()))

      case _ =>
        throw new UnsupportedOperationException
    }

    if (buf.readableBytes() > 0) {
      throw new IllegalStateException("Did not read all bytes in message " + message)
    }

    message
  }


  private def writeSnapshot(buf: ByteBuf, snapshot: Snapshot) = {
    buf.writeInt(snapshot.seed)
    buf.writeBoolean(snapshot.teamsOn)
    writeSet(buf, snapshot.players, writePlayer)
    writeSet(buf, snapshot.entities, writeEntity)
  }

  private def readSnapshot(buf: ByteBuf) =
    Snapshot(buf.readInt(),
             buf.readBoolean(),
             readSet(buf, readPlayer), LeaderBoard(),
             readSet(buf, readEntity))


  private def writePlayer(buf: ByteBuf, player: Player) = {
    writeId(buf, player.id)
    writeString(buf, player.nickname)
  }

  private def readPlayer(buf: ByteBuf) = Player(readId(buf), readString(buf))


  private def writeEntity(buf: ByteBuf, e: Entity) = e match {
    case s: ShotEntity =>
      buf.writeByte(1)
      writeShot(buf, s)

    case p: PlayerEntity =>
      buf.writeByte(2)
      writePlayerEntity(buf, p)
  }

  private def readEntity(buf: ByteBuf): Entity = {
    buf.readByte() match {
      case 1 => readShot(buf)
      case 2 => readPlayerEntity(buf)
    }
  }


  private def writeShot(buf: ByteBuf, e: ShotEntity) = {
    buf.writeLong(e.id)
    writeId(buf, e.owner)
    writeVector(buf, e.position)
    writeVector(buf, e.velocity)
    buf.writeBoolean(e.onGround)
    buf.writeFloat(e.age)
  }

  private def readShot(buf: ByteBuf) = ShotEntity(buf.readLong(), readId(buf),
                                                  readVector(buf), readVector(buf),
                                                  buf.readBoolean(), buf.readFloat())


  private def writePlayerEntity(buf: ByteBuf, e: PlayerEntity) = {
    buf.writeLong(e.id)
    writeId(buf, e.player)
    writeVector(buf, e.position)
    writeVector(buf, e.aim)
    writeVector(buf, e.velocity)
    buf.writeBoolean(e.onGround)
  }

  private def readPlayerEntity(buf: ByteBuf) = PlayerEntity(buf.readLong(), readId(buf),
                                                            readVector(buf), readVector(buf),
                                                            readVector(buf), buf.readBoolean())


  private def writeInputCommand(buf: ByteBuf, c: InputCommand) = {
    buf.writeFloat(c.run)
    buf.writeBoolean(c.jump)
    buf.writeBoolean(c.fire)
    writeVector(buf, c.aimDirection)
  }

  private def readInputCommand(buf: ByteBuf) = InputCommand(buf.readFloat(),
                                                            buf.readBoolean(),
                                                            buf.readBoolean(), readVector(buf))


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


  private def writeSet[T](buf: ByteBuf, set: Set[T], writer: (ByteBuf, T) => ByteBuf) = {
    buf.writeInt(set.size)
    set.foreach(item => writer(buf, item))
  }

  private def readSet[T](buf: ByteBuf, reader: ByteBuf => T) = {
    val count = buf.readInt()
    0.until(count).map(_ => reader(buf)).toSet
  }
}
