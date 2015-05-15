package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Event
import mountainrangepvp.game.world.ClientId

case class PlayerJoined(id: ClientId, nickname: String) extends Event

case class PlayerLeft(id: ClientId) extends Event


case class ShutdownEvent() extends Event


case class PlayerFireRequestEvent(playerId: ClientId, direction: Vector2) extends Event
