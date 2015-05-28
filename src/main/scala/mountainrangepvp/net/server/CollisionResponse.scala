package mountainrangepvp.net.server

import mountainrangepvp.game.world._

/**
 * Handles collisions between shots, terrain, and players
 */
class CollisionResponse {
  def process(snapshot: Snapshot, collisions: Set[Collision]) = {
    val toDelete = entitiesToDelete(collisions)
    val hitShots = shotsThatHit(collisions)

    val newEntities = snapshot.entities
                      .filterNot(e => toDelete.contains(e.id))
                      .filter(isAlive)

    snapshot.copy(entities = newEntities,
                  leaderBoard = snapshot.leaderBoard.addAll(hitShots))
  }


  private def entitiesToDelete(collisions: Set[Collision]): Set[Long] =
    collisions.flatMap {
      case EntityToGroundCollision(e: ShotEntity, _) =>
        List(e.id)

      case EntityToEntityCollision(player: PlayerEntity, shot: ShotEntity, _) =>
        if (killsPlayer(shot, player)) {
          List(shot.id, player.id)
        } else List.empty

      case _ => List.empty
    }

  private def killsPlayer(shot: ShotEntity, player: PlayerEntity) =
    shot.owner != player.player


  private def shotsThatHit(collisions: Set[Collision]): Set[(ClientId, ClientId)] =
    collisions.flatMap {
      case EntityToEntityCollision(player: PlayerEntity, shot: ShotEntity, _) =>
        if (killsPlayer(shot, player)) {
          Some((shot.owner, player.player))
        } else None

      case _ => None
    }


  private def isAlive(e: Entity) = e match {
    case s: ShotEntity => s.isAlive
    case _ => true
  }
}
