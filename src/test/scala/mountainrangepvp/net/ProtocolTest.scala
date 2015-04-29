package mountainrangepvp.net

import junit.framework.AssertionFailedError
import mountainrangepvp.engine.util.{Event, EventHandler, EventBus}
import mountainrangepvp.game.world.{PlayerStatsUpdatedEvent, NewMapEvent, NewSessionEvent}
import mountainrangepvp.net.client.Client
import mountainrangepvp.net.server.{Server, SessionConfig}
import org.junit.Test

class ProtocolTest {
  @Test
  def connectionTest() = {
    val eventbus = new EventBus(Thread.currentThread())
    val server = new Server(SessionConfig(teamsOn = false), () => {})
    val client = new Client(eventbus, server, "protocol test subject")
    val recorder = new EventRecorder(eventbus)

    client.start()
    server.updateTillDone()

    recorder.received(NewSessionEvent(teamsOn = false))
    recorder.received(classOf[NewMapEvent])
    recorder.received(classOf[PlayerStatsUpdatedEvent])
  }

  private class EventRecorder(eventbus: EventBus) extends EventHandler[Event] {
    private var recording: List[Event] = List.empty

    eventbus.subscribeAll(this)

    def received(event: Event): Unit = {
      if(!recording.contains(event))
        throw new AssertionFailedError("Did not record a " + event)
    }

    def received[T <: Event](eventClass: Class[T]): Unit = {
      if(!recording.exists(_.getClass == eventClass))
        throw new AssertionFailedError("Did not record a " + eventClass)
    }


    override def receive(e: Event) = {
      recording ::= e
    }
  }
}
