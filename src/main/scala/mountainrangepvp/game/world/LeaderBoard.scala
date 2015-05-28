package mountainrangepvp.game.world

import mountainrangepvp.game.world.LeaderBoard.Stats

/**
 * Holds the hit/death stats for players.
 */
object LeaderBoard {


  def apply() = new LeaderBoard(Set.empty)

  object Stats {
    def apply(player: ClientId) = new Stats(player, 0, 0)
  }

  case class Stats(player: ClientId, hits: Int, deaths: Int) {
    def plusHit = copy(hits = hits + 1)

    def plusDeath = copy(deaths = deaths + 1)

    def ratio = hits.toFloat / deaths.toFloat
  }

}

case class LeaderBoard(players: Set[Stats]) {

  def sortedByHighest = players.toList.sortBy(_.ratio).reverse

  def join(player: ClientId): LeaderBoard = LeaderBoard(players + Stats(player))

  def leave(player: ClientId): LeaderBoard = LeaderBoard(players.filterNot(_.player == player))

  def addAll(hitPairs: Iterable[(ClientId, ClientId)]): LeaderBoard =
    hitPairs.foldLeft(this)((lb, pair) => lb.add(pair))

  /**
   * Adds a hit pair to the leader board.
   * @param hitPair Shooter -> Deceased
   */
  def add(hitPair: (ClientId, ClientId)): LeaderBoard =
    copy(players.map { stats =>
      if (stats.player == hitPair._1) stats.plusHit
      else if (stats.player == hitPair._2) stats.plusDeath
      else stats
    })
}
