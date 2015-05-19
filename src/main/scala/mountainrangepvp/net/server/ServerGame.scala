package mountainrangepvp.net.server

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.{MultiLagTimer, PingMessage, PingedMessage, SnapshotMessage}

/**
 * Container of game systems.
 */
class ServerGame(log: Log, eventBus: EventBus, out: Outgoing) {

  private var _going = true

  def going: Boolean = _going


  private val _emptySnapshot = Snapshot.empty(0, teamsOn = false)

  private var _snapshot = _emptySnapshot


  private var _nextEntityId: Long = 0

  private var _clientInputState: Map[ClientId, InputState] = Map.empty


  private var _terrain: Terrain = new Terrain(new HillsHeightMap(_snapshot.seed))


  private var _multiLagTimer = MultiLagTimer()

  def lag(client: ClientId): Option[Duration] = _multiLagTimer.lagFor(client)


  def shutdown() = {
    _going = false
  }

  def update(dt: Float) = {
    sendPingQuery()
    eventBus.flushPendingMessages()
    _snapshot = processInput(dt, _snapshot)

    _snapshot = step(dt, _snapshot)


    if (_snapshot.seed != _terrain.getSeed)
      _terrain = new Terrain(new HillsHeightMap(_snapshot.seed))

    out.sendToAll(SnapshotMessage(_snapshot))
    eventBus.resetMessagesPerFrame()
  }

  private def sendPingQuery(): Unit = {
    val now = System.currentTimeMillis()
    _snapshot.players.foreach { id =>
      _multiLagTimer = _multiLagTimer.start(id.id, now)(pingId => out.send(id.id, PingMessage(pingId)))
    }
  }

  def processInput(dt: Float, snapshot: Snapshot): Snapshot = {
    var nextSnapshot = snapshot

    _clientInputState = _clientInputState.map { case (id, state) =>
      nextSnapshot = nextSnapshot.updatePlayer(id, e => {
        val newVel = e.velocity.cpy()
        if (newVel.x.abs <= PlayerEntity.RunSpeed)
          newVel.x = lerp(newVel.x, state.run * PlayerEntity.RunSpeed, if (e.onGround) 0.5f else 0.1f)
        if (state.jump && e.onGround)
          newVel.y += PlayerEntity.JumpImpulse
        e.copy(aim = state.aimDirection, velocity = newVel)
      })

      if (state.firing)
        nextSnapshot = nextSnapshot.addShot(id, state.aimDirection)

      id -> state.nextFrame(dt)
    }

    nextSnapshot
  }


  def step(dt: Float, snapshot: Snapshot): Snapshot = {
    snapshot.copy(
      shots = snapshot.shots.map(s => stepShot(dt, s)).filter(_.isAlive),
      playerEntities = snapshot.playerEntities.map(e => stepPlayerEntity(dt, e))
    )
  }

  def stepShot(dt: Float, shot: Shot) = {
    val newPos = shot.direction.cpy()
                 .scl(Shot.SHOT_SPEED * dt)
                 .add(shot.position)
    shot.copy(position = newPos, age = shot.age + dt)
  }

  def stepPlayerEntity(dt: Float, playerEntity: PlayerEntity) = {
    val oldX = playerEntity.position.x

    val newVel = playerEntity.velocity.cpy()
                 .add(0, if (playerEntity.onGround) 0 else -9.81f * 15)
    val newPos = newVel.cpy()
                 .scl(dt)
                 .add(playerEntity.position)

    var point = _terrain.getSample(newPos.x.toInt)

    if (point - newPos.y > PlayerEntity.MaxWalkingGradient) {
      val maxX = newPos.x
      val direction = if (maxX < oldX) -1 else 1
      newPos.x = oldX

      var walkable = true
      while (walkable) {
        newPos.x += direction
        point = _terrain.getSample(newPos.x.toInt)
        walkable = point - newPos.y <= PlayerEntity.MaxWalkingGradient
      }

      newPos.x -= direction
      point = _terrain.getSample(newPos.x.toInt)
    }

    val onGround = if (point > newPos.y) {
      newPos.y = point
      newVel.y = newVel.y.max(0)
      true
    } else false

    playerEntity.copy(position = newPos, velocity = newVel, onGround = onGround)
  }

  private def lerp(x: Float, target: Float, alpha: Float) =
    x + alpha * (target - x)


  eventBus.subscribe((_: ShutdownEvent) => shutdown())

  eventBus.subscribe((e: PlayerJoined) => {
    log.info(e.id + " " + e.nickname + " connected")
    _snapshot =
      _snapshot.join(e.id, e.nickname)
      .addPlayerEntity(_nextEntityId, e.id, new Vector2((Math.random() * 800 - 40).toFloat, 100))
    _clientInputState += e.id -> InputState()

    _nextEntityId += 1
  })

  eventBus.subscribe((e: PlayerLeft) => {
    log.info(e.id + " " + _snapshot.nicknameFor(e.id) + " disconnected")
    _snapshot =
      _snapshot.leave(e.id)
      .removePlayerEntity(e.id)
    _clientInputState -= e.id
  })

  eventBus.subscribe((e: PongEvent) => {
    val now = System.currentTimeMillis()
    _multiLagTimer = _multiLagTimer.stop(e.id, e.pingId, now) { time => out.send(e.id, PingedMessage(time)) }
  })

  eventBus.subscribe((e: InputCommandReceivedEvent) => {
    _clientInputState += e.playerId -> _clientInputState(e.playerId).accumulate(e.command)
  })
}
