package mountainrangepvp.net.server

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

/**
 * Holds a queue of things to do and executes them in order.
 */
class TaskQueue {
  private val _queue: BlockingQueue[() => Unit] = new LinkedBlockingQueue[() => Unit]()

  def queue(f: () => Unit) = _queue.offer(f)

  def runAll() = {
    while (hasWork)
      _queue.take().apply()
  }

  def hasWork: Boolean = !_queue.isEmpty
}
