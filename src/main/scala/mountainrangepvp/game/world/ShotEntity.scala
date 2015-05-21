package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object ShotEntity {
  val SHOT_SPEED = 2000f
  val MAX_AGE = 60f
}

case class ShotEntity(id: Long, owner: ClientId, position: Vector2, direction: Vector2, age: Float) extends Entity {
  var time = 0f

  def isAlive = age < ShotEntity.MAX_AGE
}
