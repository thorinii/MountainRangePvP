package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world._

/**
 * Steps the entities forward and computes collisions.
 */
class PhysicsSystem {

  def step(dt: Float, terrain: Terrain, snapshot: Snapshot): Snapshot = {
    snapshot.copy(
      shots = snapshot.shots.map(s => stepEntity(dt, terrain, s)).filter(_.isAlive),
      playerEntities = snapshot.playerEntities.map(e => stepEntity(dt, terrain, e))
    )
  }

  private def stepEntity[T <: Entity](dt: Float, terrain: Terrain, entity: T): T = {
    val gravity = entity.gravity

    val nvel = entity.velocity.cpy()
               .add(0, gravity)
    val npos = nvel.cpy()
               .scl(dt)
               .add(entity.position)

    val onGround = if (entity.standsOnTerrain) {
      val standing = standOnGround(terrain, entity.position, npos)
      if (standing)
        nvel.y = 0
      standing
    } else {
      collideWithGround(terrain, npos)
    }

    entity match {
      case s: ShotEntity => s.next(dt, npos, nvel, onGround).asInstanceOf[T]
      case p: PlayerEntity => p.next(dt, npos, nvel, onGround).asInstanceOf[T]
    }
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

    val onGround = if (point > npos.y) {
      npos.y = point
      true
    } else false
    onGround
  }

  private def collideWithGround(terrain: Terrain, pos: Vector2) = {
    var point = terrain.getSample(pos.x.toInt)
    point > pos.y
  }
}
