package mountainrangepvp.net.server

import mountainrangepvp.engine.util.{EventBus, Log}
import mountainrangepvp.game.world.{ChatManager, Player, PlayerManager, Session}

/**
 * The network-protocol agnostic thing that runs the world. All calls are asynchronous.
 */
object ServerThread {
  def startServer(log: Log, sessionConfig: SessionConfig): LocalServerInterface = {
    val fps = 20
    val updateInterval = 1f / fps

    val eventBus = new EventBus()
    val session = buildSession(log, eventBus, sessionConfig)

    val localInterface = new LocalServerInterface(log, eventBus)
    val server = new ServerGame(log, eventBus, localInterface, session)

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

  private def buildSession(log: Log, eventBus: EventBus, sessionConfig: SessionConfig) = {
    // TODO get rid of this
    val playerManager = new PlayerManager("", Player.Team.BLUE)
    val chatManager = new ChatManager(playerManager)

    new Session(log, eventBus, sessionConfig.teamsOn, playerManager, chatManager)
  }
}
