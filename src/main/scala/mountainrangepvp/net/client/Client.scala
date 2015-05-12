package mountainrangepvp.net.client

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net._

/**
 * That which talks to the server, whether by the network or in-process calls.
 */
object Client {
  def newClient(log: Log, eventBus: EventBus, server: ServerInterface, nickname: String): Client = {
    val c = new Client(log, eventBus, server, nickname)
    c.subscribe()
    c
  }
}

class Client(log: Log, eventBus: EventBus, server: ServerInterface, nickname: String) {
  private var id: ClientId = null
  private var online = false

  private var _lag: Duration = Duration.ZERO

  def lag = _lag


  @throws(classOf[InterruptedException])
  def start() = {
    server.connect(new ClientInterfaceImpl)
    online = true
  }

  def shutdown() = {
    online = false
    server.shutdown()
  }


  private def subscribe() = {
    eventBus.subscribe((e: FireRequestEvent) => server.receive(id, FireShotMessage(e.direction)))
  }

  private class ClientInterfaceImpl extends ClientInterface {
    override def connected(id: ClientId) = {
      Client.this.id = id
      log.setName("client" + id.id)
      server.receive(id, LoginMessage(NetworkConstants.CHECK_CODE, NetworkConstants.VERSION, nickname))
    }

    override def disconnected() = {
      if (online) {
        eventBus.send(ServerDisconnect)
      }
    }

    override def ping(pingId: Int): Unit = {
      server.receive(id, PongMessage(pingId))
    }

    override def pinged(lag: Duration): Unit = {
      _lag = lag
    }

    override def sessionInfo(teamsOn: Boolean) = {
      eventBus.send(NewSessionEvent(teamsOn))
    }

    override def newMap(seed: Int) = {
      eventBus.send(NewMapEvent(seed))
    }


    override def playerStats(stats: PlayerStats) = {
      eventBus.send(PlayerStatsUpdatedEvent(stats))
    }

    override def firedShot(client: ClientId, from: Vector2, direction: Vector2) = {
      eventBus.send(PlayerFiredEvent(client, from, direction))
    }
  }

}
