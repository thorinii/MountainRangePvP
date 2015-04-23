package mountainrangepvp.game.event

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Event
import mountainrangepvp.game.world.Player


case class NewInstanceEvent(teamsOn: Boolean) extends Event

case class PlayerFiredEvent(player: Player, source: Vector2, direction: Vector2) extends Event
