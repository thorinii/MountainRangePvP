package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Log
import mountainrangepvp.game.world.{ClientId, PlayerStats}
import mountainrangepvp.net.{ClientInterface, ServerInterface}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

class Server(sessionConfig: SessionConfig, shutdownHook: () => Unit) extends ServerInterface {
  private val log = new Log("server")
  private val nextClientId: AtomicLong = new AtomicLong(0L)
  private val interfaces: mutable.Map[ClientId, ClientInterface] = TrieMap.empty
  private val taskQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()
  private val sendQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()

  // TODO: playerStats should be in a session
  private var playerStats = new PlayerStats

  private val map = new Map(34)

  @volatile
  private var _going: Boolean = true

  def going = _going


  override def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    interfaces += id -> client

    send(client)(_.connected(id))
  }

  override def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected")

    send(client)(_.sessionInfo(sessionConfig.teamsOn))
    send(client)(_.newMap(map.seed))
    stats(_.joined(client, nickname))
  }

  override def shutdown() = {
    _going = false
    shutdownHook()
  }

  override def fireShot(client: ClientId, direction: Vector2) = {
    log.todo(client + " fired towards " + direction)
    // TODO
  }


  def update(): Unit = {
    val oldStats = playerStats

    while (!taskQueue.isEmpty) {
      val task = taskQueue.take()
      task()
    }

    if (playerStats.changedSince(oldStats)) {
      sendToAll(_.playerStats(playerStats))
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
    async(taskQueue)(() => playerStats = action(playerStats))
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
