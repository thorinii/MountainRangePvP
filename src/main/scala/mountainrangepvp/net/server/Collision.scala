package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.Entity

sealed abstract class Collision(val hitter: Entity)

case class EntityToGroundCollision(entity: Entity, at: Vector2) extends Collision(entity)


object EntityToEntityCollision {
  def of(a: Entity, b: Entity, at: Vector2) = {
    if (a.getClass.getName < b.getClass.getName)
      new EntityToEntityCollision(a, b, at)
    else
      new EntityToEntityCollision(b, a, at)
  }
}

case class EntityToEntityCollision private (a: Entity, b: Entity, at: Vector2) extends Collision(a)
