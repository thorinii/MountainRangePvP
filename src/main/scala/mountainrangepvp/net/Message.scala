package mountainrangepvp.net

import java.time.Duration

import mountainrangepvp.core.{ClientId, InputCommand, Snapshot}


sealed trait Message

sealed trait ToServerMessage extends Message

sealed trait ToClientMessage extends Message


case class ConnectedMessage(id: ClientId) extends ToClientMessage

case class LoginMessage(checkCode: Int, version: Int, nickname: String) extends ToServerMessage


case class SnapshotMessage(snapshot: Snapshot) extends ToClientMessage


case class CommandMessage(command: InputCommand) extends ToServerMessage


case class PingMessage(id: Int) extends ToClientMessage

case class PongMessage(id: Int) extends ToServerMessage

case class PingedMessage(lag: Duration) extends ToClientMessage
