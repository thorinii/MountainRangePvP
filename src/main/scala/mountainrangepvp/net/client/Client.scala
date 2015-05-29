package mountainrangepvp.net.client

import java.time.Duration

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
    eventBus.subscribe((e: InputCommandEvent) => send(CommandMessage(e.command)))
  }

  private def send(message: ToServerMessage) = {
    if (online) server.deliver(id, message)
  }

  private class ClientInterfaceImpl extends ClientInterface {
    private var loggedIn = false

    private def setLoggedIn() = if (!loggedIn) {
      eventBus.send(ConnectedEvent(id))
      loggedIn = true
    }


    override def disconnected() = {
      if (online) {
        eventBus.send(ServerDisconnect)
        online = false
      }
    }

    override def deliver(message: ToClientMessage) = message match {
      case ConnectedMessage(id) =>
        Client.this.id = id
        log.setName("client" + id.id)
        server.deliver(id, LoginMessage(NetworkConstants.CHECK_CODE, NetworkConstants.VERSION, nickname))

      case PingMessage(pingId) =>
        setLoggedIn()
        server.deliver(id, PongMessage(pingId))

      case PingedMessage(lag) =>
        setLoggedIn()
        _lag = lag

      case SnapshotMessage(snapshot) =>
        setLoggedIn()
        eventBus.send(SnapshotEvent(snapshot))
    }
  }

}
