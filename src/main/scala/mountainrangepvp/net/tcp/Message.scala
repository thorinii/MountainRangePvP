package mountainrangepvp.net.tcp

import mountainrangepvp.game.world.{ClientId, PlayerStats}


sealed trait Message

sealed trait ToServerMessage extends Message

sealed trait ToClientMessage extends Message


case class ConnectedMessage(id: ClientId) extends ToClientMessage

case class LoginMessage(checkCode: Int, version: Int, nickname: String) extends ToServerMessage


case class SessionInfoMessage(teamsOn: Boolean) extends ToClientMessage

case class NewMapMessage(seed: Int) extends ToClientMessage

case class PlayerStatsMessage(stats: PlayerStats) extends ToClientMessage
