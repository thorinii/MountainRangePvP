package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import mountainrangepvp.engine.util.Log
import mountainrangepvp.game.world.{ClientId, PlayerStats}
import mountainrangepvp.net.{ClientInterface, ServerInterface}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

/**
 * The network-protocol agnostic thing that runs the world. All calls are asynchronous.
 */
object Server {
  def startServer(sessionConfig: SessionConfig): Server = {
    val updateIntervalMillis = 100

    var s: Server = null
    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        while (s.going) {
          s.update()

          try {
            Thread.sleep(updateIntervalMillis)
          } catch {
            case _: InterruptedException => // do nothing; the while loop will stop when it's time
          }
        }
      }
    }, "Server Update")

    s = new Server(sessionConfig, () => thread.interrupt())
    thread.start()
    s
  }
}


class Server(sessionConfig: SessionConfig, shutdownHook: () => Unit) extends ServerInterface {
  private val nextClientId: AtomicLong = new AtomicLong(0L)
  private val interfaces: mutable.Map[ClientId, ClientInterface] = TrieMap.empty
  private val taskQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()
  private val sendQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()
  private var playerStats = new PlayerStats

  // TODO: make this a setting in current map
  private val seed = 34

  @volatile
  private var going: Boolean = true


  def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    interfaces += id -> client

    send(client)(_.connected(id))
  }

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    Log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected")

    send(client)(_.sessionInfo(sessionConfig.teamsOn))
    send(client)(_.newMap(seed))
    stats(_.joined(client, nickname))
  }

  def shutdown() = {
    going = false
    shutdownHook()
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
