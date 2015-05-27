package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * A player on screen. The thing that runs around shooting things. The thing that can die and <em>be not</em>.
 * Not all players/clients have a PlayerEntity under their name at a given time.
 */
object PlayerEntity {
  val Width = 40
  val Height = 100
  val RunSpeed = 400
  val JumpImpulse = 1000
  val MaxWalkingGradient = 30
  val GunHeight = 57
}

case class PlayerEntity(id: Long, player: ClientId,
                        position: Vector2, aim: Vector2,
                        velocity: Vector2, onGround: Boolean) extends Entity {
  override val bounds = Rectangle(position.cpy().sub(PlayerEntity.Width/2, 0),
                                  position.cpy().add(PlayerEntity.Width/2, PlayerEntity.Height))

  def gravity = if (onGround) 0 else -9.81f * 15

  val standsOnTerrain = true

  def next(dt: Float, npos: Vector2, nvel: Vector2, onGround: Boolean) =
    copy(position = npos, velocity = nvel, onGround = onGround)
}
