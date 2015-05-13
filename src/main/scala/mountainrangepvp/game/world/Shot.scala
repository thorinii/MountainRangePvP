package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object Shot {
  val SHOT_SPEED: Int = 2000
}

case class Shot(playerId: ClientId, base: Vector2, direction: Vector2) {
  var time = 0f

  def position: Vector2 = position(time)

  def position(time: Float): Vector2 = {
    base.cpy.add(direction.cpy.scl(Shot.SHOT_SPEED * time))
  }
}
