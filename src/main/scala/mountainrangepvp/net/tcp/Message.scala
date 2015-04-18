package mountainrangepvp.net.tcp

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.ClientId

trait Message

case class ConnectedMessage(id: ClientId) extends Message

case class LoginMessage(checkCode: Int, version: Int, nickname: String) extends Message


object Message {
  def decode(buf: ByteBuf): Message = {
    Log.info("decoded message")
    ConnectedMessage(new ClientId(178293))
  }

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

    case _ =>
      Log.todo()
  }
}
