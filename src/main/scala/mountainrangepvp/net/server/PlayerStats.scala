package mountainrangepvp.net.server

import mountainrangepvp.net.ClientId

/**
 * Stores player nickname and hit/kill statistics.
 */
case class PlayerStats(players: Map[ClientId, String], private val changes: Int) {
  def this(players: Map[ClientId, String]) = this(players, 0)

  def this() = this(Map.empty)


  def joined(id: ClientId, nickname: String) = {
    PlayerStats(players + (id -> nickname), changes + 1)
  }

  def changedSince(previous: PlayerStats) = changes > previous.changes
}
