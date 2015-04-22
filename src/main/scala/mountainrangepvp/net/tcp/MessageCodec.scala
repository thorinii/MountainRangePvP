package mountainrangepvp.net.tcp

import java.nio.charset.StandardCharsets

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.ClientId

/**
 * De/encodes messages into Netty {@link ByteBuf}s
 */
object MessageCodec {

  def send(ctx: ChannelHandlerContext, msg: Message): Unit = {
    ctx.writeAndFlush(encode(ctx, msg)).sync
    Log.info("Sent " + msg)
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

    case InstanceInfoMessage() =>
      buf.writeInt(3)

    case _ =>
      Log.todoCrash()
      throw new UnsupportedOperationException
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
        InstanceInfoMessage()

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