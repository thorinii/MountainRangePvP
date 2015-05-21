package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object ShotEntity {
  val Speed = 2000f
  val MaxAge = 60f
}

case class ShotEntity(id: Long, owner: ClientId,
                      position: Vector2, velocity: Vector2,
                      age: Float) extends Entity {
  var time = 0f

  def isAlive = age < ShotEntity.MaxAge
}
