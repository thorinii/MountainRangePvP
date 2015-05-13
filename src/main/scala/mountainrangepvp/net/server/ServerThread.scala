package mountainrangepvp.net.server

import mountainrangepvp.engine.util.{EventBus, Log}

/**
 * The network-protocol agnostic thing that runs the world. All calls are asynchronous.
 */
object ServerThread {
  def startServer(log: Log, sessionConfig: SessionConfig): LocalServerInterface = {
    val fps = 20
    val updateInterval = 1f / fps

    val eventBus = new EventBus()

    val localInterface = new LocalServerInterface(log, eventBus)
    val server = new ServerGame(log, eventBus, localInterface)

    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        while (server.going) {
          server.update(updateInterval)

          try {
            Thread.sleep((updateInterval * 1000).toInt)
          } catch {
            case _: InterruptedException => // do nothing; the while loop will stop when it's time
          }
        }
      }
    }, "Server Update")

    eventBus.setDispatchThread(thread)
    thread.start()
    localInterface
  }
}
