package mountainrangepvp.net.tcp

import mountainrangepvp.net.ClientId

trait Message

case class ConnectedMessage(id: ClientId) extends Message

case class LoginMessage(checkCode: Int, version: Int, nickname: String) extends Message
