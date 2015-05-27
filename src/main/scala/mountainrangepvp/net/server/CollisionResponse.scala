package mountainrangepvp.net.server

import mountainrangepvp.game.world.{Entity, PlayerEntity, ShotEntity, Snapshot}

/**
 * Handles collisions between shots, terrain, and players
 */
class CollisionResponse {
  def process(snapshot: Snapshot, collisions: Set[Collision]) = {
    val toDelete: Set[Long] = collisions.map {
      case EntityToGroundCollision(e: ShotEntity, _) =>
        List(e.id)

      case EntityToEntityCollision(player: PlayerEntity, shot: ShotEntity, _) =>
        if (killsPlayer(shot, player)) {
          List(shot.id, player.id)
        } else List.empty

      case _ => List.empty
    }.flatten

    val newEntities = snapshot.entities
                      .filterNot(e => toDelete.contains(e.id))
                      .filter(isAlive)
    snapshot.copy(entities = newEntities)
  }

  private def killsPlayer(shot: ShotEntity, player: PlayerEntity): Boolean = {
    shot.owner != player.player
  }

  private def isAlive(e: Entity) = e match {
    case s: ShotEntity => s.isAlive
    case _ => true
  }
}
