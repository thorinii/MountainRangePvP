package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.{ClientInterface, ServerInterface}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

class LocalServerInterface(log: Log, eventBus: EventBus) extends ServerInterface with Outgoing {
  private val nextClientId: AtomicLong = new AtomicLong(0L)
  private val interfaces: mutable.Map[ClientId, ClientInterface] = TrieMap.empty


  override def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    interfaces += id -> client

    client.connected(id)
  }

  override def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    eventBus.send(PlayerJoined(client, nickname))
  }

  override def disconnect(client: ClientId) = {
    eventBus.send(PlayerLeft(client))
    interfaces -= client
  }

  override def pong(id: ClientId, pingId: Int): Unit = {
  }

  override def fireShot(client: ClientId, direction: Vector2) = {
    // TODO actually put in world and simulate
    sendToAll(_.firedShot(client, new Vector2(0, 0), direction))
  }

  def shutdown() = eventBus.send(ShutdownEvent())


  private def send(interface: ClientInterface, action: ClientInterface => Unit): Unit = {
    action(interface)
  }

  override def send(id: ClientId, action: ClientInterface => Unit): Unit = {
    action(interfaces(id))
  }

  private def sendToAll(action: ClientInterface => Unit): Unit = {
    interfaces.values.foreach(i => send(i, action))
  }

  private def sendToAllExcept(id: ClientId, action: ClientInterface => Unit): Unit = {
    interfaces.filterKeys(_ != id).values.foreach(i => send(i, action))
  }


  private type Action = () => Unit
}
