package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong

import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.{ClientId, ClientInterface, ServerInterface}

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
  private var clients: Map[ClientId, ClientSendQueue] = Map.empty

  @volatile
  private var going: Boolean = true

  def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    Log.info(id + " connected")

    clients += id -> new ClientSendQueue(id, client).sendConnected()
  }

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    Log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected")
    clients(client).sendInstanceInfo()
  }

  def shutdown() = {
    going = false
    thread.interrupt()
  }

  private def update(): Unit = {
    clients.values.foreach(_.update())
  }
}
