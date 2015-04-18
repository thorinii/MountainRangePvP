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
          try {
            s.update()
            Thread.sleep(updateIntervalMillis)
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

  def connect(client: ClientInterface) {
    val id: ClientId = new ClientId(nextClientId.getAndIncrement)
    Log.info(id + " connected")

    clientHandlers += id -> new ClientHandler(id, client)
  }

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String) {
    Log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected")
    clientHandlers(client).login()
  }

  def shutdown() {
    going = false
    thread.interrupt()
  }

  private def update(): Unit = {
    clientHandlers.values.foreach(_.update())
  }

  private class ClientHandler(id: ClientId, interface: ClientInterface) {
    var update: () => Unit = connected

    def login() = update = nil // TODO: send instance info message; move to send map info

    private def connected(): Unit = {
      interface.connected(id)

      update = nil
    }

    private def nil(): Unit = {}
  }

}
