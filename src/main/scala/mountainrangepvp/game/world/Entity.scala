package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * A simulated object in the world.
 */
abstract class Entity {
  val id: Long
  val position: Vector2
  val velocity: Vector2
  val onGround: Boolean

  def gravity: Float

  val standsOnTerrain: Boolean

  override final def equals(obj: scala.Any): Boolean = obj match {
    case o: Entity => o.id == this.id && o.getClass == this.getClass
    case _ => false
  }
}
