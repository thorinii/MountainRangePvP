package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Event


case class NewSessionEvent(teamsOn: Boolean) extends Event

case class NewMapEvent(seed: Int) extends Event

case class PlayerFiredEvent(player: Player, source: Vector2, direction: Vector2) extends Event