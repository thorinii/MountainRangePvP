package mountainrangepvp.net.tcp

import mountainrangepvp.net.ClientId


sealed trait Message

sealed trait ToServerMessage extends Message
sealed trait ToClientMessage extends Message


case class ConnectedMessage(id: ClientId) extends ToClientMessage

case class LoginMessage(checkCode: Int, version: Int, nickname: String) extends ToServerMessage

case class InstanceInfoMessage(teamsOn: Boolean) extends ToClientMessage
