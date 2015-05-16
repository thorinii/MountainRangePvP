package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net._

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

class LocalServerInterface(log: Log, eventBus: EventBus) extends ServerInterface with Outgoing {
  private val nextClientId: AtomicLong = new AtomicLong(0L)
  private val interfaces: mutable.Map[ClientId, ClientInterface] = TrieMap.empty


  override def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    interfaces += id -> client

    client.deliver(ConnectedMessage(id))
  }

  override def disconnect(client: ClientId) = {
    eventBus.send(PlayerLeft(client))
    interfaces -= client
  }

  override def shutdown() = eventBus.send(ShutdownEvent())

  /**
   * Receive a message from a client
   */
  override def deliver(client: ClientId, message: ToServerMessage) = message match {
    case LoginMessage(checkCode, version, nickname) =>
      eventBus.send(PlayerJoined(client, nickname))

    case PongMessage(id) =>
    // TODO: send to ServerGame

    case CommandMessage(command) =>
      eventBus.send(InputCommandReceivedEvent(client, command))
  }


  override def send(id: ClientId, message: ToClientMessage): Unit = {
    interfaces(id).deliver(message)
  }

  override def sendToAll(message: ToClientMessage): Unit = {
    interfaces.values.foreach(_.deliver(message))
  }


  private type Action = () => Unit
}
