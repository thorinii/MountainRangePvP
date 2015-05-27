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
                      onGround: Boolean, age: Float) extends Entity {
  override val bounds = Point(position)

  var time = 0f

  def isAlive = age < ShotEntity.MaxAge

  val gravity = 0f

  val standsOnTerrain = false

  def next(dt: Float, npos: Vector2, nvel: Vector2, onGround: Boolean) =
    copy(position = npos, velocity = nvel, age = age + dt, onGround = onGround)
}
