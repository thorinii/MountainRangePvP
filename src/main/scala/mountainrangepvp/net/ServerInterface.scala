package mountainrangepvp.net

import mountainrangepvp.core.ClientId

/**
 * The interface the client talks to, either in-process or over the network.
 */
trait ServerInterface {
  /**
   * A client wishes to connect.
   */
  def connect(client: ClientInterface)

  /**
   * A client has disconnected.
   */
  def disconnect(client: ClientId)

  /**
   * A command to shutdown the interface.
   */
  def shutdown()


  /**
   * Send a message to the server.
   */
  def deliver(clientId: ClientId, message: ToServerMessage)
}
