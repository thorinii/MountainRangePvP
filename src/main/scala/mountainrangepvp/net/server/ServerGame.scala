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

  private val _physicsSystem = new PhysicsSystem

  private val _inputSystem = new InputSystem


  private var _going = true

  def going: Boolean = _going


  private val _emptySnapshot = Snapshot.empty(0, teamsOn = false)

  private var _snapshot = _emptySnapshot


  private var _nextEntityId: Long = 0


  private var _terrain: Terrain = new Terrain(new HillsHeightMap(_snapshot.seed))


  private var _multiLagTimer = MultiLagTimer()

  def lag(client: ClientId): Option[Duration] = _multiLagTimer.lagFor(client)


  def shutdown() = {
    _going = false
  }

  def update(dt: Float) = {
    sendPingQuery()
    eventBus.flushPendingMessages()

    _snapshot = _inputSystem.process(dt, _snapshot)
    _snapshot = _physicsSystem.step(dt, _terrain, _snapshot)

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


  eventBus.subscribe((_: ShutdownEvent) => shutdown())

  eventBus.subscribe((e: PlayerJoined) => {
    log.info(e.id + " " + e.nickname + " connected")
    _snapshot =
      _snapshot.join(e.id, e.nickname)
      .addPlayerEntity(_nextEntityId, e.id, new Vector2((Math.random() * 800 - 40).toFloat, 100))
    _inputSystem.join(e.id)

    _nextEntityId += 1
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
