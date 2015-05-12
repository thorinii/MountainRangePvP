package mountainrangepvp.net

import java.time.Duration

import com.badlogic.gdx.math.Vector2
import junit.framework.AssertionFailedError
import mountainrangepvp.engine.util.{Event, EventBus, EventHandler, Log}
import mountainrangepvp.game.world._
import mountainrangepvp.net.client.Client
import mountainrangepvp.net.server.LocalServerInterface
import org.junit.Test

import scala.collection.{Map => SMap}

class LocalServerInterfaceTest {
  val serverLog = new Log("testserver")
  val serverBus = new EventBus()
  val server = new LocalServerInterface(serverLog, serverBus)
  val clientRecorder = new RecordingClient(server)

  @Test
  def connectionTest() = {
    clientRecorder.connect()

    assert(clientRecorder.id.isValid, "did not call connected(id)")
  }

  class RecordingClient(server: ServerInterface) extends ClientInterface {
    var id: ClientId = ClientId.Invalid

    def connect() = {
      server.connect(this)
    }

    override def connected(id: ClientId): Unit = {
      this.id = id
    }

    /**
     * When the socket closes (may be called during normal shutdown, not just when the server disconnects)
     */
    override def disconnected(): Unit = ???

    /**
     * The server's response to the Pong message
     */
    override def pinged(lag: Duration): Unit = ???

    override def newMap(seed: Int): Unit = ???

    override def firedShot(client: ClientId, from: Vector2, direction: Vector2): Unit = ???

    override def sessionInfo(teamsOn: Boolean): Unit = ???

    /**
     * The server requests a Pong message
     */
    override def ping(pingId: Int): Unit = ???

    override def playerStats(stats: PlayerStats): Unit = ???
  }
}
