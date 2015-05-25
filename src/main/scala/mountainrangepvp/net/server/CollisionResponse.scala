package mountainrangepvp.net.server

import mountainrangepvp.game.world.{Entity, ShotEntity, Snapshot}

/**
 * Handles collisions between shots, terrain, and players
 */
class CollisionResponse {
  def process(snapshot: Snapshot, collisions: Set[Collision]) = {
    var toDelete = Set.empty[Long]
    collisions.foreach {
      case EntityToGroundCollision(e: ShotEntity, _) =>
        toDelete += e.id

      case _ => None
    }

    if (snapshot.entities.nonEmpty)
      println(snapshot.entities.groupBy(_.id).map(_._2.size).toList.max)

    val newEntities = snapshot.entities
                      .filterNot(e => toDelete.contains(e.id))
                      .filter(isAlive)
    snapshot.copy(entities = newEntities)
  }

  def isAlive(e: Entity) = e match {
    case s: ShotEntity => s.isAlive
    case _ => true
  }
}
