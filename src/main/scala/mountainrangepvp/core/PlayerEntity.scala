package mountainrangepvp.core

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
  val BubbleTime = 5f
  val BubbleRadius = 51f

  def apply(id: Long, player: ClientId, position: Vector2) =
    new PlayerEntity(id, player,
                     position,
                     velocity = new Vector2(0, 0),
                     aim = new Vector2(0, 0),
                     onGround = false, bubbleTimer = BubbleTime)
}

case class PlayerEntity(id: Long, player: ClientId,
                        position: Vector2, aim: Vector2,
                        velocity: Vector2, onGround: Boolean,
                        bubbleTimer: Float) extends Entity {
  override val bounds = Rectangle(position.cpy().sub(PlayerEntity.Width / 2, 0),
                                  position.cpy().add(PlayerEntity.Width / 2, PlayerEntity.Height))

  def gravity = if (onGround) 0 else -9.81f * 15

  val standsOnTerrain = true

  def hasBubble = bubbleTimer > 0

  def next(dt: Float, npos: Vector2, nvel: Vector2, onGround: Boolean) =
    copy(position = npos, velocity = nvel, onGround = onGround, bubbleTimer = bubbleTimer - dt)
}
