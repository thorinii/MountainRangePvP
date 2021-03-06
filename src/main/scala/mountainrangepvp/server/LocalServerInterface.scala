package mountainrangepvp.server

import java.util.concurrent.atomic.AtomicLong

import mountainrangepvp.core._
import mountainrangepvp.engine.Log
import mountainrangepvp.engine.util.EventBus
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

    case PongMessage(pingId) =>
      eventBus.send(PongEvent(client, pingId))

    case CommandMessage(command) =>
      eventBus.send(InputCommandReceivedEvent(client, command))
  }


  override def send(id: ClientId, message: ToClientMessage): Unit = {
    interfaces.get(id).foreach(_.deliver(message))
  }

  override def sendToAll(message: ToClientMessage): Unit = {
    interfaces.values.foreach(_.deliver(message))
  }


  private type Action = () => Unit
}
