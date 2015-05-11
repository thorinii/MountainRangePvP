package mountainrangepvp.net

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import junit.framework.AssertionFailedError
import mountainrangepvp.engine.util.{Event, EventBus, EventHandler, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.client.Client
import mountainrangepvp.net.server.LocalServerInterface
import org.junit.Test

import scala.collection.{Map => SMap}

class LocalServerInterfaceTest {
  val serverLog = new Log("testserver")
  val clientLog = new Log("testclient")
  val serverBus = new EventBus()
  val server = new LocalServerInterface(serverLog, serverBus)
  val recorder = new EventRecorder(serverBus)

  @Test
  def connectionTest() = {
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

    assert(id != null, "did not call connected(id)")
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
