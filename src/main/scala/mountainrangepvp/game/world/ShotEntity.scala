package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object ShotEntity {
  val Speed = 2000f
  val MaxAge = 60f

  def apply(id: Long, owner: ClientId, base: Vector2, direction: Vector2) =
    new ShotEntity(id, owner,
               base, direction.cpy().scl(ShotEntity.Speed),
               onGround = false, age = 0f)
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

  def retarget(from: Vector2, direction: Vector2) = copy(position = from,
                                                         velocity = direction.cpy().scl(ShotEntity.Speed))
}
