package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.Entity

sealed trait Collision

case class EntityToGroundCollision(entity: Entity, at: Vector2) extends Collision

case class EntityToEntityCollision(a: Entity, b: Entity, at: Vector2) extends Collision
