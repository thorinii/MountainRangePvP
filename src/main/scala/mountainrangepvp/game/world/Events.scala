package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Event


case class NewSessionEvent(teamsOn: Boolean) extends Event

case class NewMapEvent(seed: Int) extends Event


case class PlayerStatsUpdatedEvent(stats: PlayerStats) extends Event

case class FireRequestEvent(direction: Vector2) extends Event
