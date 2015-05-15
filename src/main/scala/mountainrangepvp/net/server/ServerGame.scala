package mountainrangepvp.net.server

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{ClientId, Shot, Snapshot}
import mountainrangepvp.net.{MultiLagTimer, SessionInfoMessage, SnapshotMessage}

/**
 * Container of game systems.
 */
class ServerGame(log: Log, eventBus: EventBus, out: Outgoing) {

  private var _going = true

  def going: Boolean = _going


  private val _emptySnapshot = Snapshot.empty(0, false)

  private var _snapshot = _emptySnapshot


  private var _nextEntityId: Long = 0


  @volatile
  private var _multiLagTimer = MultiLagTimer()

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

    _snapshot = step(dt, _snapshot)

    out.sendToAll(SnapshotMessage(_snapshot))
    eventBus.resetMessagesPerFrame()
  }


  eventBus.subscribe((_: ShutdownEvent) => shutdown())

  eventBus.subscribe((e: PlayerJoined) => {
    log.info(e.id + " " + e.nickname + " connected")
    _snapshot =
      _snapshot.join(e.id, e.nickname)
      .addPlayerEntity(_nextEntityId, e.id, new Vector2(0, 0))

    _nextEntityId += 1

    out.send(e.id, SessionInfoMessage(_snapshot.teamsOn))
  })

  eventBus.subscribe((e: PlayerLeft) => {
    log.info(e.id + " disconnected")
    _snapshot =
      _snapshot.leave(e.id)
      .removePlayerEntity(e.id)
  })

  eventBus.subscribe((e: PlayerFireRequestEvent) => {
    _snapshot = _snapshot.addShot(e.playerId, new Vector2(0, 0), e.direction)
  })


  def step(dt: Float, snapshot: Snapshot): Snapshot = {
    snapshot.copy(
      shots = snapshot.shots.map(s => stepShot(dt, s)).filter(_.isAlive)
    )
  }

  def stepShot(dt: Float, shot: Shot) = {
    val newpos = shot.direction.cpy()
                 .scl(Shot.SHOT_SPEED * dt)
                 .add(shot.position)
    shot.copy(position = newpos, age = shot.age + dt)
  }
}
