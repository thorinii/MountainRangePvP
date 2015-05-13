package mountainrangepvp.net.server

import java.time.Duration

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{ClientId, NewMapEvent}
import mountainrangepvp.net.{MultiLagTimer, NewMapMessage, SessionInfoMessage}

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

    out.send(e.id, SessionInfoMessage(_snapshot.teamsOn))
    out.send(e.id, NewMapMessage(_snapshot.seed))

    _snapshot = _snapshot.join(e.id, e.nickname)
  })

  eventBus.subscribe((e: PlayerLeft) => {
    log.info(e.id + " disconnected")
    // Note may not be fully logged in
  })


  eventBus.send(NewMapEvent(0))
}
