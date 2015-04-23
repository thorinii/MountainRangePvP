package mountainrangepvp.net.server

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import mountainrangepvp.net.{ClientId, ClientInterface}

/**
 * Asynchronously sends messages to the client.
 */
private class ClientSendQueue(id: ClientId, interface: ClientInterface) {
  type UpdateFunction = () => Unit
  private val queue: BlockingQueue[UpdateFunction] = new LinkedBlockingQueue[UpdateFunction]()

  def update() = {
    while (!queue.isEmpty) {
      val msg = queue.take()
      msg()
    }
  }

  def sendConnected() = pushSend {
    interface.connected(id)
  }

  def sendInstanceInfo(teamsOn: Boolean) = pushSend {
    interface.instanceInfo(teamsOn)
  }

  private def pushSend(a: => Unit) = {
    queue.offer(() => {
      a
    })
    this
  }
}
