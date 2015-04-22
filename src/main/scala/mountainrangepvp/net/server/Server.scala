package mountainrangepvp.net.server

import java.util.concurrent.atomic.AtomicLong

import mountainrangepvp.engine.util.Log
import mountainrangepvp.net.{ClientId, ClientInterface, ServerInterface}

import scala.collection.immutable.Queue

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
  private var clientHandlers: Map[ClientId, ClientHandler] = Map.empty

  @volatile
  private var going: Boolean = true

  def connect(client: ClientInterface) = {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    Log.info(id + " connected")

    clientHandlers += id -> new ClientHandler(id, client).sendConnected()
  }

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String) = {
    Log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected")
    clientHandlers(client).sendInstanceInfo()
  }

  def shutdown() = {
    going = false
    thread.interrupt()
  }

  private def update(): Unit = {
    clientHandlers.values.foreach(_.update())
  }

  private class ClientHandler(id: ClientId, interface: ClientInterface) {
    type UpdateFunction = () => Unit
    private var queue: Queue[UpdateFunction] = Queue.empty

    def update() = {
      queue.foreach(_.apply())
      queue = Queue.empty
    }

    def sendConnected() = pushSend {
      interface.connected(id)
    }

    def sendInstanceInfo() = pushSend {
      interface.instanceInfo()
    }

    private def pushSend(a: => Unit) = {
      queue = queue.enqueue(() => a)
      this
    }
  }

}
