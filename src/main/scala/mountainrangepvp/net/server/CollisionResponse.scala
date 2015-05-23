package mountainrangepvp.net.server

import mountainrangepvp.game.world.{Entity, ShotEntity, Snapshot}

/**
 * Handles collisions between shots, terrain, and players
 */
class CollisionResponse {
  def process(snapshot: Snapshot, collisions: Set[Collision]) = {
    snapshot.copy(entities = snapshot.entities.filter(oldShots))
  }

  def oldShots(e: Entity) = e match {
    case s: ShotEntity if !s.isAlive => false
    case _ => true
  }
}
