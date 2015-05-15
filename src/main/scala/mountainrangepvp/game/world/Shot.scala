package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object Shot {
  val SHOT_SPEED = 2000f
  val MAX_AGE = 60f
}

case class Shot(owner: ClientId, position: Vector2, direction: Vector2, age: Float) {
  var time = 0f

  def isAlive = age < Shot.MAX_AGE
}
