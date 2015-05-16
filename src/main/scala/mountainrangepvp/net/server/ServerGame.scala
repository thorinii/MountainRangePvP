package mountainrangepvp.net.server

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.{MultiLagTimer, SnapshotMessage}

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


  private val _multiLagTimer = MultiLagTimer()

  def lag(client: ClientId): Option[Duration] = _multiLagTimer.lagFor(client)

  def sendPingQuery(): Unit = {
  }

  def pong(id: ClientId, pingId: Int): Unit = {
  }


  def shutdown() = {
    _going = false
  }

  def update(dt: Float) = {
    eventBus.flushPendingMessages()
    _snapshot = processInput(dt, _snapshot)

    _snapshot = step(dt, _snapshot)


    if (_snapshot.seed != _terrain.getSeed)
      _terrain = new Terrain(new HillsHeightMap(_snapshot.seed))

    out.sendToAll(SnapshotMessage(_snapshot))
    eventBus.resetMessagesPerFrame()
  }


  def processInput(dt: Float, snapshot: Snapshot): Snapshot = {
    var nextSnapshot = snapshot

    _clientInputState = _clientInputState.map { case (id, state) =>
      nextSnapshot = nextSnapshot
                     .playerUpdate(id, state.run, state.aimDirection)

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
    val newVel = playerEntity.velocity.cpy()
                 .add(0, -9.81f * 1)
    val newPos = newVel.cpy()
                 .scl(dt)
                 .add(playerEntity.position)

    val slice = _terrain.getSlice(newPos.x.toInt, PlayerEntity.Width)
    val highestPoint = slice.getHighestPoint

    if (newPos.y < highestPoint) {
      newPos.y = highestPoint
      if (newVel.y < 0 || newVel.y < 1)
        newVel.y = 0
    }

    if (newVel.x.abs < 1)
      newVel.x = 0

    playerEntity.copy(position = newPos, velocity = newVel)
  }


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

  eventBus.subscribe((e: InputCommandReceivedEvent) => {
    _clientInputState += e.playerId -> _clientInputState(e.playerId).accumulate(e.command)
  })
}
