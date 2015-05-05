package mountainrangepvp.game.world

import scala.collection.{Map => SMap}

/**
 * Stores player nickname and hit/kill statistics.
 */
case class PlayerStats(players: SMap[ClientId, String], private val changes: Int) {
  def this(players: SMap[ClientId, String]) = this(players, 0)

  def this() = this(SMap.empty)


  def joined(id: ClientId, nickname: String) = next(players + (id -> nickname))

  def left(id: ClientId) = next(players - id)

  def changedSince(previous: PlayerStats) = changes > previous.changes


  private def next(players: SMap[ClientId, String]) = {
    PlayerStats(players, changes + 1)
  }
}
