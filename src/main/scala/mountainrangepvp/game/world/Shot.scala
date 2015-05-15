package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object Shot {
  val SHOT_SPEED = 2000f
}

case class Shot(owner: ClientId, position: Vector2, direction: Vector2) {
  var time = 0f
}
