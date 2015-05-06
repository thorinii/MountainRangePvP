package mountainrangepvp.net

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.{ClientId, PlayerStats}

/**
 * The interface the server talks to, either in-process or over the network.
 */
trait ClientInterface {
  /**
   * Tells a client that they've successfully connected.
   */
  def connected(id: ClientId)

  /**
   * When the socket closes (may be called during normal shutdown, not just when the server disconnects)
   */
  def disconnected()


  def sessionInfo(teamsOn: Boolean)

  def newMap(seed: Int)


  def playerStats(stats: PlayerStats)


  def firedShot(client: ClientId, from: Vector2, direction: Vector2)
}
