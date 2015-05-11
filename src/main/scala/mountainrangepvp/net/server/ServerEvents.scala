package mountainrangepvp.net.server

import mountainrangepvp.engine.util.Event
import mountainrangepvp.game.world.ClientId

case class PlayerJoined(id: ClientId, nickname: String) extends Event

case class PlayerLeft(id: ClientId) extends Event


case class ShutdownEvent() extends Event
