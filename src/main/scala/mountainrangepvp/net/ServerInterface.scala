package mountainrangepvp.net

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.ClientId

/**
 * The interface the client talks to, either in-process or over the network.
 */
trait ServerInterface {
  def connect(client: ClientInterface)

  def login(client: ClientId, checkCode: Int, version: Int, nickname: String)

  def shutdown()


  def fireShot(client: ClientId, direction: Vector2)
}
