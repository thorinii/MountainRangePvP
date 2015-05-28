package mountainrangepvp.game.world

import mountainrangepvp.game.world.LeaderBoard.Stats

/**
 * Holds the hit/death stats for players.
 */
object LeaderBoard {
  def apply() = new LeaderBoard(Map.empty)

  object Stats {
    def apply() = new Stats(0, 0)
  }

  case class Stats(hits: Int, deaths: Int) {
    def plusHit = Stats(hits + 1, deaths)

    def plusDeath = Stats(hits, deaths + 1)
  }

}

case class LeaderBoard(players: Map[ClientId, Stats]) {
  def join(player: ClientId): LeaderBoard = LeaderBoard(players + (player -> Stats()))

  def leave(player: ClientId): LeaderBoard = LeaderBoard(players - player)

  def addAll(hitPairs: Iterable[(ClientId, ClientId)]): LeaderBoard =
    hitPairs.foldLeft(this)((lb, pair) => lb.add(pair))

  /**
   * Adds a hit pair to the leader board.
   * @param hitPair Killer -> Deceased
   * @return
   */
  def add(hitPair: (ClientId, ClientId)): LeaderBoard =
    copy(players.map {
      case (id, stats) if id == hitPair._1 => id -> stats.plusHit
      case (id, stats) if id == hitPair._2 => id -> stats.plusDeath
      case e => e
    })

}
