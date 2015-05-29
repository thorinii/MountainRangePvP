package mountainrangepvp.net

/**
 * The interface the server talks to, either in-process or over the network.
 */
trait ClientInterface {
  /**
   * The server closes the connection.
   */
  def disconnected()

  /**
   * Receive a message from the server.
   */
  def deliver(message: ToClientMessage)
}
