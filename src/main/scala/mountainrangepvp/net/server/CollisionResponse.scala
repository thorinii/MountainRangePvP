package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.core._

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
            case s: ShotEntity if s.id == shot.id =>
              ricochet(s, player)
            case e => e
          }
        else if (killsPlayer(shot, player))
          entities.filterNot(_.id == shot.id).filterNot(_.id == player.id)
        else entities

      case _ => entities
    })

  private def bouncesOffPlayer(shot: ShotEntity, player: PlayerEntity) =
    player.hasBubble && (if (shot.owner == player.player) shot.age > 0.2 else true)

  private def killsPlayer(shot: ShotEntity, player: PlayerEntity) =
    shot.owner != player.player && !player.hasBubble

  private def ricochet(shot: ShotEntity, player: PlayerEntity) = {
    val playerCentre = player.position.cpy().add(0, PlayerEntity.Height / 2f)
    val shotNow = shot.position
    val shotBefore = shot.velocity.cpy().scl(-1).add(shotNow)

    val intersection = getLineCircleIntersection(shotBefore, shotNow,
                                                 playerCentre, PlayerEntity.BubbleRadius + 1)
    intersection.map { intersection =>
      val direction = intersection.cpy().sub(playerCentre).nor()
      shot.retarget(intersection, direction)
    }.getOrElse(shot)
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

  /**
   * Calculates the intersection point between a line and a circle. If there's
   * two points, it returns the one closest to <code>l1</code>.
   *
   * Copied from some place on the Internet; I have no idea how it works except
   * that there's a quadratic formula in there...
   */
  private def getLineCircleIntersection(l1: Vector2, l2: Vector2,
                                        circle: Vector2, radius: Float): Option[Vector2] = {
    val baX = l2.x - l1.x
    val baY = l2.y - l1.y
    val caX = circle.x - l1.x
    val caY = circle.y - l1.y

    val a = baX * baX + baY * baY
    val bBy2 = baX * caX + baY * caY
    val c = caX * caX + caY * caY - radius * radius

    val pBy2 = bBy2 / a
    val q = c / a

    val disc = pBy2 * pBy2 - q
    if (disc < 0) {
      None
    } else {
      val tmpSqrt = Math.sqrt(disc).toFloat
      val abScalingFactor1 = -pBy2 + tmpSqrt
      val abScalingFactor2 = -pBy2 - tmpSqrt

      val i1: Vector2 = new Vector2(l1.x - baX * abScalingFactor1,
                                    l1.y - baY * abScalingFactor1)
      if (disc == 0) {
        Some(i1)
      } else {
        val i2: Vector2 = new Vector2(l1.x - baX * abScalingFactor2,
                                      l1.y - baY * abScalingFactor2)
        if (l1.dst(i1) < l1.dst(i2))
          Some(i1)
        else
          Some(i2)
      }
    }
  }
}
