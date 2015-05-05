package mountainrangepvp.net

import junit.framework.AssertionFailedError
import mountainrangepvp.engine.util.{Event, EventBus, EventHandler, Log}
import mountainrangepvp.game.world.{NewMapEvent, NewSessionEvent, PlayerStatsUpdatedEvent, Session}
import mountainrangepvp.net.client.Client
import mountainrangepvp.net.server.Server
import org.junit.Test

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


  class EventRecorder(eventbus: EventBus) extends EventHandler[Event] {
    private var recording: List[Event] = List.empty

    eventbus.subscribeAll(this)

    def received(event: Event): Unit = {
      if (!recording.contains(event))
        throw new AssertionFailedError("Did not record a " + event)
    }

    def received[T <: Event](eventClass: Class[T]): Unit = {
      if (!recording.exists(_.getClass == eventClass))
        throw new AssertionFailedError("Did not record a " + eventClass)
    }


    override def receive(e: Event) = {
      recording ::= e
    }
  }

}
