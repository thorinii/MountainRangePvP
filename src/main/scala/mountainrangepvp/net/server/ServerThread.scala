package mountainrangepvp.net.server

import mountainrangepvp.engine.util.{EventBus, Log}

import scala.concurrent.duration._

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
        try {
          while (server.going) {
            frame()
          }
        } catch {
          case _: InterruptedException => // just kill the thread
          case e: Exception =>
            log.crash("Uncaught exception in ServerThread", e)
            throw e
        }
      }

      private def frame() = {
        val start = System.nanoTime()
        server.update(updateInterval)
        val end = System.nanoTime()

        val delta = (end - start).nanos.toMillis
        val sleep = (updateInterval * 1000).toInt - delta

        if (sleep > 1)
          Thread.sleep((updateInterval * 1000).toInt)
      }
    }, "Server Update")

    thread.start()
    localInterface
  }

}
