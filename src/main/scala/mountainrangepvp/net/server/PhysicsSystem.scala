package mountainrangepvp.net.server

import mountainrangepvp.game.world.{Snapshot, Shot, Terrain, PlayerEntity}

/**
 * Steps the entities forward and computes collisions.
 */
class PhysicsSystem {

  def step(dt: Float, terrain: Terrain, snapshot: Snapshot): Snapshot = {
    snapshot.copy(
      shots = snapshot.shots.map(s => stepShot(dt, s)).filter(_.isAlive),
      playerEntities = snapshot.playerEntities.map(e => stepPlayerEntity(dt, terrain, e))
    )
  }

  def stepShot(dt: Float, shot: Shot) = {
    val newPos = shot.direction.cpy()
                 .scl(Shot.SHOT_SPEED * dt)
                 .add(shot.position)
    shot.copy(position = newPos, age = shot.age + dt)
  }

  def stepPlayerEntity(dt: Float, terrain: Terrain, playerEntity: PlayerEntity) = {
    val oldX = playerEntity.position.x

    val newVel = playerEntity.velocity.cpy()
                 .add(0, if (playerEntity.onGround) 0 else -9.81f * 15)
    val newPos = newVel.cpy()
                 .scl(dt)
                 .add(playerEntity.position)

    var point = terrain.getSample(newPos.x.toInt)

    if (point - newPos.y > PlayerEntity.MaxWalkingGradient) {
      val maxX = newPos.x
      val direction = if (maxX < oldX) -1 else 1
      newPos.x = oldX

      var walkable = true
      while (walkable) {
        newPos.x += direction
        point = terrain.getSample(newPos.x.toInt)
        walkable = point - newPos.y <= PlayerEntity.MaxWalkingGradient
      }

      newPos.x -= direction
      point = terrain.getSample(newPos.x.toInt)
    }

    val onGround = if (point > newPos.y) {
      newPos.y = point
      newVel.y = newVel.y.max(0)
      true
    } else false

    playerEntity.copy(position = newPos, velocity = newVel, onGround = onGround)
  }
}
