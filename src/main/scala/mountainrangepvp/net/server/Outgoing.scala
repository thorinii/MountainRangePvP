package mountainrangepvp.net.server

import mountainrangepvp.core.ClientId
import mountainrangepvp.net.ToClientMessage

/**
 * Takes the messages from the server and sends them to the appropriate clients.
 */
trait Outgoing {
  def send(id: ClientId, message: ToClientMessage): Unit

  def sendToAll(message: ToClientMessage): Unit
}
