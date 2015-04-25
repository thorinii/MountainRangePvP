package mountainrangepvp.engine.util

import java.util
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.reflect.ClassTag


trait EventHandler[T <: Event] {
  def receive(event: T)
}


/**
 * A pub/sub message passing system.
 */
class EventBus(dispatchThread: Thread) {
  private val dispatchers: mutable.Map[Class[_ <: Event], Dispatcher[_ <: Event]] = new TrieMap[Class[_ <: Event], Dispatcher[_ <: Event]]
  private val pendingMessages: util.Queue[Event] = new LinkedBlockingQueue[Event]
  private val messagesPerFrame: AtomicInteger = new AtomicInteger(0)

  def subscribe[T <: Event](eventClass: Class[T], handler: EventHandler[T]): Unit = {
    val d: Dispatcher[T] = dispatchers.get(eventClass).map(_.asInstanceOf[Dispatcher[T]]).getOrElse {
      val d = new Dispatcher[T]
      dispatchers += (eventClass -> d)
      d
    }

    d.add(handler)
  }

  def subscribe[T <: Event](handler: T => Unit)(implicit tag: ClassTag[T]): Unit = {
    subscribe(tag.runtimeClass.asInstanceOf[Class[T]], new EventHandler[T] {
      override def receive(event: T): Unit = handler(event)
    })
  }

  def send(event: Event) = {
    if (Thread.currentThread eq dispatchThread) dispatch(event)
    else pendingMessages.offer(event)
  }

  private def dispatch(event: Event) = {
    messagesPerFrame.incrementAndGet

    dispatchers.get(event.getClass) match {
      case Some(d) => d.dispatch(event)
      case _ => Log.fine("No handlers subscribed to " + event)
    }
  }

  /**
   * Assumes running on the dispatch thread.
   */
  def flushPendingMessages() = {
    while (!pendingMessages.isEmpty) dispatch(pendingMessages.remove)
  }

  def resetMessagesPerFrame() = {
    messagesPerFrame.set(0)
  }

  def getMessagesPerFrame: Int = {
    messagesPerFrame.get
  }

  private class Dispatcher[T <: Event] {
    private var handlers: List[EventHandler[T]] = List.empty

    def add(handler: EventHandler[T]) {
      handlers = handler :: handlers
    }

    def dispatch(e: Event) {
      dispatchGeneric(e.asInstanceOf[T])
    }

    private def dispatchGeneric(e: T) {
      for (h <- handlers) {
        h.receive(e.asInstanceOf[T])
      }
    }
  }

}
