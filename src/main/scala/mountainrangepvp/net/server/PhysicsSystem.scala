package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world._

/**
 * Steps the entities forward and computes collisions.
 */
class PhysicsSystem {

  def step(dt: Float, terrain: Terrain, snapshot: Snapshot): (Snapshot, Set[Collision]) = {
    val (stepped, collisions) =
      snapshot.entities.map(e => stepEntity(dt, terrain, e)).
      foldLeft((Set.empty[Entity], Set.empty[Collision])) {
        case ((es, cs), (e, c)) => (es + e, cs ++ c)
      }


    val nextSnapshot = snapshot.copy(
      entities = stepped
    )

    (nextSnapshot, collisions)
  }

  private def stepEntity(dt: Float, terrain: Terrain, entity: Entity): (Entity, List[Collision]) = {
    val nvel = entity.velocity.cpy()
               .add(0, entity.gravity)
    val npos = nvel.cpy()
               .scl(dt)
               .add(entity.position)

    val groundCollision: Option[EntityToGroundCollision] = if (entity.standsOnTerrain) {
      val standing = standOnGround(terrain, entity.position, npos)
      if (standing.isDefined)
        nvel.y = nvel.y.max(0)
      standing.map(p => EntityToGroundCollision(entity, p))
    } else {
      collideWithGround(terrain, npos).map(p => EntityToGroundCollision(entity, p))
    }

    val nextEntity = entity match {
      case s: ShotEntity => s.next(dt, npos, nvel, groundCollision.isDefined)
      case p: PlayerEntity => p.next(dt, npos, nvel, groundCollision.isDefined)
    }

    (nextEntity, groundCollision.toList)
  }

  private def standOnGround(terrain: Terrain, opos: Vector2, npos: Vector2) = {
    val oldX = opos.x

    var point = terrain.getSample(npos.x.toInt)

    if (point - npos.y > PlayerEntity.MaxWalkingGradient) {
      val maxX = npos.x
      val direction = if (maxX < oldX) -1 else 1
      npos.x = oldX

      var walkable = true
      while (walkable) {
        npos.x += direction
        point = terrain.getSample(npos.x.toInt)
        walkable = point - npos.y <= PlayerEntity.MaxWalkingGradient
      }

      npos.x -= direction
      point = terrain.getSample(npos.x.toInt)
    }

    if (point > npos.y) {
      npos.y = point
      Some(new Vector2(npos.x, point))
    } else None
  }

  private def collideWithGround(terrain: Terrain, pos: Vector2) = {
    var point = terrain.getSample(pos.x.toInt)
    if (point > pos.y) Some(new Vector2(pos.x, point))
    else None
  }
}
