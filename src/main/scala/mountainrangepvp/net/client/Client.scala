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
    override def disconnected() = {
      if (online) {
        eventBus.send(ServerDisconnect)
      }
    }

    override def receive(message: ToClientMessage) = message match {
      case ConnectedMessage(id) =>
        Client.this.id = id
        log.setName("client" + id.id)
        server.receive(id, LoginMessage(NetworkConstants.CHECK_CODE, NetworkConstants.VERSION, nickname))

      case PingMessage(pingId) =>
        server.receive(id, PongMessage(pingId))

      case PingedMessage(lag) =>
        _lag = lag

      case SessionInfoMessage(teamsOn) =>
        eventBus.send(NewSessionEvent(teamsOn))

      case NewMapMessage(seed) =>
        eventBus.send(NewMapEvent(seed))

      case PlayerStatsMessage(stats) =>
        eventBus.send(PlayerStatsUpdatedEvent(stats))

      case PlayerFiredMessage(client, from, direction) =>
        eventBus.send(PlayerFiredEvent(client, from, direction))
    }
  }

}
