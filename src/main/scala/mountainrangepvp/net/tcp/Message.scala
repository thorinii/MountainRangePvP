package mountainrangepvp.net.tcp

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.{ClientId, PlayerStats}


sealed trait Message

sealed trait ToServerMessage extends Message

sealed trait ToClientMessage extends Message


case class ConnectedMessage(id: ClientId) extends ToClientMessage

case class LoginMessage(checkCode: Int, version: Int, nickname: String) extends ToServerMessage


case class SessionInfoMessage(teamsOn: Boolean) extends ToClientMessage

case class NewMapMessage(seed: Int) extends ToClientMessage

case class PlayerStatsMessage(stats: PlayerStats) extends ToClientMessage


case class FireShotMessage(direction: Vector2) extends ToServerMessage

case class PlayerFiredMessage(client: ClientId, from: Vector2, direction: Vector2) extends ToClientMessage


case class PingMessage(id: Int) extends ToClientMessage

case class PongMessage(id: Int) extends ToServerMessage

case class PingedMessage(lag: Duration) extends ToClientMessage
