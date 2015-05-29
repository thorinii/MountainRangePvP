package mountainrangepvp.net.server

import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.{MultiLagTimer, PingMessage, PingedMessage, SnapshotMessage}

/**
 * Container of game systems.
 */
class ServerGame(log: Log, eventBus: EventBus, out: Outgoing) {

  private val _nextEntityId = new AtomicLong(0)

  private val idGenerator = () => _nextEntityId.incrementAndGet()


  private val _physicsSystem = new PhysicsSystem

  private val _inputSystem = new InputSystem(idGenerator)

  private val _collisionResponse = new CollisionResponse()


  private var _going = true

  def going: Boolean = _going


  private val _emptySnapshot = Snapshot.empty(0, teamsOn = false)

  private var _snapshot = _emptySnapshot


  private var _terrain: Terrain = new Terrain(new HillsHeightMap(_snapshot.seed))


  private var _multiLagTimer = MultiLagTimer()

  def lag(client: ClientId): Option[Duration] = _multiLagTimer.lagFor(client)


  def shutdown() = {
    _going = false
  }

  def update(dt: Float) = {
    sendPingQuery()
    eventBus.flushPendingMessages()

    if (_snapshot.seed != _terrain.getSeed)
      _terrain = new Terrain(new HillsHeightMap(_snapshot.seed))

    _snapshot = _inputSystem.process(dt, _snapshot)
    val (physicsSnapshot, collisions) = _physicsSystem.step(dt, _terrain, _snapshot)
    _snapshot = _collisionResponse.process(physicsSnapshot, collisions)
    _snapshot = _snapshot.tickTimers(dt)
    _snapshot = processTimers(_snapshot)


    out.sendToAll(SnapshotMessage(_snapshot))
    eventBus.resetMessagesPerFrame()
  }

  private def sendPingQuery(): Unit = {
    val now = System.currentTimeMillis()
    _snapshot.players.foreach { id =>
      _multiLagTimer = _multiLagTimer.start(id.id, now)(pingId => out.send(id.id, PingMessage(pingId)))
    }
  }

  private def processTimers(snapshot: Snapshot): Snapshot = {
    val nextSnapshot = snapshot.respawnTimers.filter(_.expired).foldLeft(snapshot) { (next, timer) =>
      next.addPlayerEntity(idGenerator(), timer.player, new Vector2((Math.random() * 800 - 40).toFloat, 100))
    }
    nextSnapshot.headlessPlayers.foldLeft(nextSnapshot) { (next, player) =>
      next.addRespawnTimer(player.id, 3)
    }.removeExpiredTimers
  }


  eventBus.subscribe((_: ShutdownEvent) => shutdown())

  eventBus.subscribe((e: PlayerJoined) => {
    log.info(e.id + " " + e.nickname + " connected")
    _snapshot =
      _snapshot.join(e.id, e.nickname)
      .addPlayerEntity(idGenerator(), e.id, new Vector2((Math.random() * 800 - 40).toFloat, 100))
    _inputSystem.join(e.id)
  })

  eventBus.subscribe((e: PlayerLeft) => {
    log.info(e.id + " " + _snapshot.nicknameFor(e.id) + " disconnected")
    _snapshot =
      _snapshot.leave(e.id)
      .removePlayerEntity(e.id)
    _inputSystem.leave(e.id)
  })

  eventBus.subscribe((e: PongEvent) => {
    val now = System.currentTimeMillis()
    _multiLagTimer = _multiLagTimer.stop(e.id, e.pingId, now)(time => out.send(e.id, PingedMessage(time)))
  })

  eventBus.subscribe((e: InputCommandReceivedEvent) => {
    _inputSystem.applyCommand(e.playerId, e.command)
  })
}
