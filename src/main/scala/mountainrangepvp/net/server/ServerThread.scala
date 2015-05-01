package mountainrangepvp.net.server

import mountainrangepvp.engine.util.Log

/**
 * The network-protocol agnostic thing that runs the world. All calls are asynchronous.
 */
object ServerThread {
  def startServer(log: Log, sessionConfig: SessionConfig): Server = {
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

    s = new Server(log, sessionConfig, () => thread.interrupt())
    thread.start()
    s
  }
}
