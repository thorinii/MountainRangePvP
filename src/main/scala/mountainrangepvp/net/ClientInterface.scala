package mountainrangepvp.net

import mountainrangepvp.game.world.{ClientId, PlayerStats}

/**
 * The interface the server talks to, either in-process or over the network.
 */
trait ClientInterface {
  /**
   * Tells a client that they've successfully connected.
   */
  def connected(id: ClientId)

  def sessionInfo(teamsOn: Boolean)

  def newMap(seed: Int)

  def playerStats(stats: PlayerStats)
}
