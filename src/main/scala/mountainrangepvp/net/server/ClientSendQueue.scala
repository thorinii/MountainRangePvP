package mountainrangepvp.net.server

import mountainrangepvp.net.{ClientId, ClientInterface}

import scala.collection.immutable.Queue

/**
 * Asynchronously sends messages to the client.
 */
private class ClientSendQueue(id: ClientId, interface: ClientInterface) {
  type UpdateFunction = () => Unit
  private var queue: Queue[UpdateFunction] = Queue.empty

  def update() = {
    queue.foreach(_.apply())
    queue = Queue.empty
  }

  def sendConnected() = pushSend {
    interface.connected(id)
  }

  def sendInstanceInfo(teamsOn: Boolean) = pushSend {
    interface.instanceInfo(teamsOn)
  }

  private def pushSend(a: => Unit) = {
    queue = queue.enqueue(() => a)
    this
  }
}
