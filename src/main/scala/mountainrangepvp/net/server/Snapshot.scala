package mountainrangepvp.net.server

import mountainrangepvp.game.world.{ClientId, Shot}

/**
 * The game state at a point in time.
 */
object Snapshot {
  def empty(seed: Int, teamsOn: Boolean) = Snapshot(seed, teamsOn, List.empty, List.empty)
}

case class Snapshot(seed: Int,
                    teamsOn: Boolean,
                    players: List[Player],
                    shots: List[Shot]) {

  def join(playerId: ClientId, nickname: String) = copy(players = Player(playerId, nickname) :: players)
}

case class Player(id: ClientId, nickname: String)
