package mountainrangepvp.net.server

import java.time.Duration

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{ClientId, NewMapEvent, Session}
import mountainrangepvp.net.MultiLagTimer

/**
 * Container of game systems.
 */
class ServerGame(log: Log, eventBus: EventBus, out: Outgoing, session: Session) {
  private val taskQueue = new TaskQueue

  private var _going = true

  def going: Boolean = _going


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
    // TODO: anything else?
  }

  def update(dt: Float) = {
    eventBus.flushPendingMessages()
    eventBus.resetMessagesPerFrame()
  }


  eventBus.subscribe((_: ShutdownEvent) => shutdown())

  eventBus.subscribe((e: PlayerJoined) => {
    out.send(e.id, _.sessionInfo(session.areTeamsOn))
    out.send(e.id, _.newMap(session.getMap.getSeed))

    // TODO: stats(_.joined(client, nickname))
  })


  eventBus.send(NewMapEvent(0))
}
