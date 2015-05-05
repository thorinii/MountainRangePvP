package mountainrangepvp.net.server

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{ChatManager, Player, PlayerManager, Session}

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


    val eventBus = new EventBus(thread)
    val session = buildSession(log, eventBus, sessionConfig)

    s = new Server(log, eventBus, session, () => thread.interrupt())
    thread.start()
    s
  }

  private def buildSession(log: Log, eventBus: EventBus, sessionConfig: SessionConfig) = {
    // TODO get rid of this
    val playerManager = new PlayerManager("", Player.Team.BLUE)
    val chatManager = new ChatManager(playerManager)

    new Session(log, eventBus, sessionConfig.teamsOn, playerManager, chatManager)
  }
}
