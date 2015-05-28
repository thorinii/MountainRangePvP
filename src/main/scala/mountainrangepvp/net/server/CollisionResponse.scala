package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world._

/**
 * Handles collisions between shots, terrain, and players
 */
class CollisionResponse {
  def process(snapshot: Snapshot, collisions: Set[Collision]) = {
    val hitShots = shotsThatHit(collisions)
    val newEntities = processEntities(snapshot, collisions)
                      .filter(isAlive)

    snapshot.copy(entities = newEntities,
                  leaderBoard = snapshot.leaderBoard.addAll(hitShots))
  }


  private def processEntities(snapshot: Snapshot, collisions: Set[Collision]): Set[Entity] =
    collisions.foldLeft(snapshot.entities)((entities, collision) => collision match {
      case EntityToGroundCollision(e: ShotEntity, _) =>
        entities.filterNot(_.id == e.id)

      case EntityToEntityCollision(player: PlayerEntity, shot: ShotEntity, _) =>
        if (bouncesOffPlayer(shot, player))
          entities.map {
            case s: ShotEntity if s.id == shot.id => ricochet(s, player)
            case e => e
          }
        else if (killsPlayer(shot, player))
          entities.filterNot(_.id == shot.id).filterNot(_.id == player.id)
        else entities

      case _ => entities
    })

  private def bouncesOffPlayer(shot: ShotEntity, player: PlayerEntity) =
    shot.owner != player.player && player.hasBubble

  private def killsPlayer(shot: ShotEntity, player: PlayerEntity) =
    shot.owner != player.player && !player.hasBubble

  private def ricochet(shot: ShotEntity, player: PlayerEntity) = {
    val playerCentre = player.position.cpy().add(0, PlayerEntity.Height / 2f)

    val direction = shot.position.cpy().sub(playerCentre).nor()
    val position = direction.cpy().scl(PlayerEntity.BubbleRadius).add(playerCentre)

    shot.retarget(position, direction)
  }


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
