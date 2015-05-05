package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{NewMapEvent, ClientId, PlayerStats, Session}
import mountainrangepvp.net.{ClientInterface, ServerInterface}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

class Server(log: Log, eventBus: EventBus, session: Session, shutdownHook: () => Unit) extends ServerInterface {
  private val nextClientId: AtomicLong = new AtomicLong(0L)
  private val interfaces: mutable.Map[ClientId, ClientInterface] = TrieMap.empty
  private val taskQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()
  private val sendQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()

  @volatile
  private var _going: Boolean = true

  def going = _going

  eventBus.send(NewMapEvent(0))


  override def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    interfaces += id -> client

    send(client)(_.connected(id))
  }

  override def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    log.info(client + " " + checkCode + "," + version + " " + nickname + " connected")

    send(client)(_.sessionInfo(session.areTeamsOn))
    send(client)(_.newMap(session.getMap.getSeed))
    stats(_.joined(client, nickname))
  }

  override def disconnect(client: ClientId) = {
    log.info(client + " " + session.getStats.players.get(client).map(_ + " ").getOrElse("") + "disconnected")

    stats(_.left(client))
    interfaces -= client
  }

  override def shutdown() = {
    _going = false
    shutdownHook()
  }

  override def fireShot(client: ClientId, direction: Vector2) = {
    // TODO actually put in world and simulate
    sendToAll(_.firedShot(client, new Vector2(0, 0), direction))
  }


  def update(): Unit = {
    val oldStats = session.getStats
    eventBus.flushPendingMessages()

    while (!taskQueue.isEmpty) {
      val task = taskQueue.take()
      task()
    }

    if (session.getStats.changedSince(oldStats)) {
      sendToAll(_.playerStats(session.getStats))
    }

    while (!sendQueue.isEmpty) {
      val msg = sendQueue.take()
      msg()
    }
  }

  def updateTillDone() = {
    do {
      update()
    } while (!taskQueue.isEmpty || !sendQueue.isEmpty)
  }


  private def async(queue: BlockingQueue[Action])(action: Action): Unit = {
    queue.offer(action)
  }

  private def stats(action: PlayerStats => PlayerStats): Unit = {
    async(taskQueue)(() => session.setStats(action(session.getStats)))
  }

  private def send(interface: ClientInterface)(action: SendAction): Unit = {
    async(sendQueue)(() => action(interface))
  }

  private def send(id: ClientId)(action: SendAction): Unit = {
    send(interfaces(id))(action)
  }

  private def sendToAll(action: SendAction): Unit = {
    interfaces.values.foreach(i => send(i)(action))
  }

  private def sendToAllExcept(id: ClientId)(action: SendAction): Unit = {
    interfaces.filterKeys(_ != id).values.foreach(i => send(i)(action))
  }


  private type Action = () => Unit
  private type SendAction = ClientInterface => Unit
}
