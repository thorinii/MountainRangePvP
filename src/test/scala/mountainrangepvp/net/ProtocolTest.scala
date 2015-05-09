package mountainrangepvp.net

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import junit.framework.AssertionFailedError
import mountainrangepvp.engine.util.{Event, EventBus, EventHandler, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.client.Client
import mountainrangepvp.net.server.Server
import org.junit.Test

import scala.collection.{Map => SMap}

class ProtocolTest {
  val serverLog = new Log("test")
  val clientLog = new Log("test")
  val eventBus = new EventBus(Thread.currentThread())
  val server = new Server(serverLog, eventBus, new Session(serverLog, eventBus, false, null, null), () => {})
  val client = new Client(clientLog, eventBus, server, "protocol test subject")
  val recorder = new EventRecorder(eventBus)

  @Test
  def connectionTest() = {
    client.start()
    server.updateTillDone()

    recorder.received(NewSessionEvent(teamsOn = false))
    recorder.received(classOf[NewMapEvent])
    recorder.received(classOf[PlayerStatsUpdatedEvent])
  }

  @Test
  def disconnectBeforeLogin() = {
    var id: ClientId = null
    server.connect(new ClientInterface {
      override def connected(i: ClientId): Unit = {
        id = i
      }

      override def disconnected(): Unit = {}

      override def ping(pingId: Int): Unit = {}

      override def pinged(lag: Duration): Unit = {}

      override def newMap(seed: Int): Unit = {}

      override def firedShot(client: ClientId, from: Vector2, direction: Vector2): Unit = {}

      override def sessionInfo(teamsOn: Boolean): Unit = {}

      override def playerStats(stats: PlayerStats): Unit = {}
    })
    server.updateTillDone()

    server.disconnect(id)
    server.updateTillDone()

    assert(server.session.getStats.players.isEmpty)
  }

  @Test
  def disconnectAfterLogin() = {
    var id: ClientId = null
    server.connect(new ClientInterface {
      override def connected(i: ClientId): Unit = {
        id = i
      }

      override def disconnected(): Unit = {}

      override def ping(pingId: Int): Unit = {}

      override def pinged(lag: Duration): Unit = {}

      override def newMap(seed: Int): Unit = {}

      override def firedShot(client: ClientId, from: Vector2, direction: Vector2): Unit = {}

      override def sessionInfo(teamsOn: Boolean): Unit = {}

      override def playerStats(stats: PlayerStats): Unit = {}
    })
    server.updateTillDone()
    server.login(id, NetworkConstants.CHECK_CODE, NetworkConstants.VERSION, "test player")
    server.updateTillDone()

    assert(server.session.getStats.players.size == 1)

    server.disconnect(id)
    server.updateTillDone()

    assert(server.session.getStats.players.isEmpty)
  }


  class EventRecorder(eventbus: EventBus) extends EventHandler[Event] {
    private var recording: List[Event] = List.empty

    eventbus.subscribeAll(this)

    def received(event: Event): Unit = {
      if (!recording.contains(event))
        throw new AssertionFailedError("Did not record a " + event + ". Instead:\n  " + recording.mkString("\n  "))
    }

    def received[T <: Event](eventClass: Class[T]): Unit = {
      if (!recording.exists(_.getClass == eventClass))
        throw new AssertionFailedError("Did not record a " + eventClass + ". Instead:\n  " + recording.mkString("\n  "))
    }


    override def receive(e: Event) = {
      recording ::= e
    }
  }

}
