package mountainrangepvp.net.client

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{NewMapEvent, NewSessionEvent, PlayerFiredEvent}
import mountainrangepvp.net.{ClientId, ClientInterface, NetworkConstants, ServerInterface}

/**
 * That which talks to the server, whether by the network or in-process calls.
 */
object Client {
  def newClient(eventbus: EventBus, server: ServerInterface, nickname: String): Client = {
    val c = new Client(eventbus, server, nickname)
    c.subscribe()
    c
  }
}

class Client(eventbus: EventBus, server: ServerInterface, nickname: String) {
  private var id: ClientId = null

  private def subscribe() = {
    eventbus.subscribe((e: PlayerFiredEvent) => Log.todo())
  }

  @throws(classOf[InterruptedException])
  def start() = {
    server.connect(new ClientInterfaceImpl)
  }

  def shutdown() = {
    server.shutdown()
  }


  private class ClientInterfaceImpl extends ClientInterface {
    def connected(id: ClientId) = {
      Client.this.id = id
      server.login(id, NetworkConstants.CHECK_CODE, NetworkConstants.VERSION, nickname)
    }

    def sessionInfo(teamsOn: Boolean) = {
      eventbus.send(new NewSessionEvent(teamsOn))
    }

    def newMap(seed: Int) = {
      eventbus.send(new NewMapEvent(seed))
    }
  }

}
