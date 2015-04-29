package mountainrangepvp.game.world

import scala.collection.{Map => SMap}

/**
 * Stores player nickname and hit/kill statistics.
 */
case class PlayerStats(players: SMap[ClientId, String], private val changes: Int) {
  def this(players: SMap[ClientId, String]) = this(players, 0)

  def this() = this(SMap.empty)


  def joined(id: ClientId, nickname: String) = {
    PlayerStats(players + (id -> nickname), changes + 1)
  }

  def changedSince(previous: PlayerStats) = changes > previous.changes
}
