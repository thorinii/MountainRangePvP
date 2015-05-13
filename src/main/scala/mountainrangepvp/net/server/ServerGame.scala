package mountainrangepvp.net.server

import java.time.Duration

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{ClientId, Snapshot}
import mountainrangepvp.net.{MultiLagTimer, SessionInfoMessage, SnapshotMessage}

/**
 * Container of game systems.
 */
class ServerGame(log: Log, eventBus: EventBus, out: Outgoing) {

  private var _going = true

  def going: Boolean = _going


  private var _emptySnapshot = Snapshot.empty(0, false)

  private var _snapshot = _emptySnapshot


  @volatile
  private var _multiLagTimer = MultiLagTimer()

  def lag(client: ClientId): Option[Duration] = _multiLagTimer.lagFor(client)


  def sendPingQuery(): Unit = {
    /*interfaces.foreach { case (id, int) =>
      val now = System.currentTimeMillis()

      taskQueue.queue { () =>
        _multiLagTimer = _multiLagTimer.start(id, now) { pingId =>
          send(id, _.ping(pingId))
        }
      }
    }*/
  }

  def pong(id: ClientId, pingId: Int): Unit = {
    /*val now = System.currentTimeMillis()

    taskQueue.queue { () =>
      _multiLagTimer = _multiLagTimer.stop(id, pingId, now) { time =>
        send(id, _.pinged(time))
      }
    }*/
  }

  def shutdown() = {
    _going = false
  }

  def update(dt: Float) = {
    eventBus.flushPendingMessages()
    eventBus.resetMessagesPerFrame()
  }


  eventBus.subscribe((_: ShutdownEvent) => shutdown())

  eventBus.subscribe((e: PlayerJoined) => {
    log.info(e.id + " " + e.nickname + " connected")
    _snapshot = _snapshot.join(e.id, e.nickname)

    out.send(e.id, SessionInfoMessage(_snapshot.teamsOn))
    out.sendToAll(SnapshotMessage(_snapshot))
  })

  eventBus.subscribe((e: PlayerLeft) => {
    log.info(e.id + " disconnected")

    val updated = _snapshot.leave(e.id)

    if (updated != _snapshot) {
      _snapshot = updated
      out.sendToAll(SnapshotMessage(_snapshot))
    }
  })
}
