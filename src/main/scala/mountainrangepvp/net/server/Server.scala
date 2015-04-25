package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.{ClientId, ClientInterface, ServerInterface}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

/**
 * The network-protocol agnostic thing that runs the world. All calls are asynchronous.
 */
object Server {
  def startServer: Server = {
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

    s = new Server(thread)
    thread.start()
    s
  }
}


class Server(val thread: Thread) extends ServerInterface {
  private val nextClientId: AtomicLong = new AtomicLong(0L)
  private val interfaces: mutable.Map[ClientId, ClientInterface] = TrieMap.empty
  private val sendQueue: BlockingQueue[Action] = new LinkedBlockingQueue[Action]()

  // TODO: make this a setting on an SessionConfig
  private val teamsOn = false

  @volatile
  private var going: Boolean = true


  def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    interfaces += id -> client

    send(client)(_.connected(id))
  }

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    Log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected")

    send(client)(_.sessionInfo(teamsOn))
  }

  def shutdown() = {
    going = false
    thread.interrupt()
  }


  private def update(): Unit = {
    while (!sendQueue.isEmpty) {
      val msg = sendQueue.take()
      msg()
    }
  }


  private def send(interface: ClientInterface)(action: SendAction): Unit = {
    sendQueue.offer(() => action(interface))
  }

  private def send(id: ClientId)(action: SendAction): Unit = {
    send(interfaces(id))(action)
  }


  private type Action = () => Unit
  private type SendAction = ClientInterface => Unit
}
