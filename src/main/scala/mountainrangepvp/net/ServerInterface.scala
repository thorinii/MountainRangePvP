package mountainrangepvp.net

/**
 * The interface the client talks to, either in-process or over the network.
 */
trait ServerInterface {
  def connect(client: ClientInterface)

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String)

  def shutdown()
}
