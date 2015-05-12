package mountainrangepvp.net

import mountainrangepvp.game.world.ClientId

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
   * Receive a message from a client
   */
  def receive(clientId: ClientId, message: ToServerMessage)
}
