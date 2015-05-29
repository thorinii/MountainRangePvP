package mountainrangepvp.net.server

import mountainrangepvp.engine.util.Event
import mountainrangepvp.game.world.{ClientId, InputCommand}

case class PlayerJoined(id: ClientId, nickname: String) extends Event

case class PlayerLeft(id: ClientId) extends Event


case class ShutdownEvent() extends Event

case class PongEvent(id: ClientId, pingId: Int) extends Event


case class InputCommandReceivedEvent(playerId: ClientId, command: InputCommand) extends Event
