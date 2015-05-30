package mountainrangepvp.net

import mountainrangepvp.engine.Log
import mountainrangepvp.engine.util.EventBus
import mountainrangepvp.core._
import mountainrangepvp.net.server.LocalServerInterface
import org.junit.Test

class LocalServerInterfaceTest {
  val serverLog = new Log("testserver")
  val serverBus = new EventBus()
  val server = new LocalServerInterface(serverLog, serverBus)
  val clientRecorder = new RecordingClient(server)

  @Test
  def getsIdWhenConnects() = {
    clientRecorder.connect()

    assert(clientRecorder.id.isValid, "did not call connected(id)")
  }

  class RecordingClient(server: ServerInterface) extends ClientInterface {
    var id: ClientId = ClientId.Invalid

    def connect() = {
      server.connect(this)
    }

    def login() = {
      connect()
    }

    override def disconnected() = {}

    override def deliver(message: ToClientMessage): Unit = message match {
      case ConnectedMessage(id) =>
        this.id = id

      case _ =>
        println("Unknown message " + message)
    }
  }

}
