package mountainrangepvp.net.client

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net._

/**
 * That which talks to the server, whether by the network or in-process calls.
 */
object Client {
  def newClient(log: Log, eventbus: EventBus, server: ServerInterface, nickname: String): Client = {
    val c = new Client(log, eventbus, server, nickname)
    c.subscribe()
    c
  }
}

class Client(log: Log, eventbus: EventBus, server: ServerInterface, nickname: String) {
  private var id: ClientId = null

  @throws(classOf[InterruptedException])
  def start() = {
    server.connect(new ClientInterfaceImpl)
  }

  def shutdown() = {
    server.shutdown()
  }


  private def subscribe() = {
    eventbus.subscribe((e: FireRequestEvent) => server.fireShot(id, e.direction))
  }

  private class ClientInterfaceImpl extends ClientInterface {
    override def connected(id: ClientId) = {
      Client.this.id = id
      log.setName("client" + id.id)
      server.login(id, NetworkConstants.CHECK_CODE, NetworkConstants.VERSION, nickname)
    }

    override def sessionInfo(teamsOn: Boolean) = {
      eventbus.send(NewSessionEvent(teamsOn))
    }

    override def newMap(seed: Int) = {
      eventbus.send(NewMapEvent(seed))
    }

    override def playerStats(stats: PlayerStats): Unit = {
      eventbus.send(PlayerStatsUpdatedEvent(stats))
    }
  }

}
