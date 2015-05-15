package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * The game state at a point in time.
 */
object Snapshot {
  def empty(seed: Int, teamsOn: Boolean) = Snapshot(seed, teamsOn,
                                                    Set.empty, Set.empty, Set.empty)
}

case class Snapshot(seed: Int,
                    teamsOn: Boolean,
                    players: Set[Player],
                    playerEntities: Set[PlayerEntity],
                    shots: Set[Shot]) {

  def join(playerId: ClientId, nickname: String) = copy(players = players + Player(playerId, nickname))

  def leave(playerId: ClientId) = copy(players = players.filterNot(_.id == playerId))


  def addShot(playerId: ClientId, base: Vector2, direction: Vector2) =
    copy(shots = shots + Shot(playerId, base, direction, 0f))


  def addPlayerEntity(entityId: Long, playerId: ClientId, position: Vector2) = {
    println("adding " + entityId + " " + playerId)
    copy(playerEntities = playerEntities + PlayerEntity(entityId, playerId, position))
  }

  def removePlayerEntity(playerId: ClientId) =
    copy(playerEntities = playerEntities.filterNot(_.player == playerId))

  def hasPlayerEntity(playerId: ClientId) = playerEntities.exists(_.player == playerId)

  def getPlayerEntity(playerId: ClientId) = playerEntities.find(_.player == playerId).get


  def nicknameFor(playerId: ClientId) = players.find(_.id == playerId).map(_.nickname).getOrElse("<UNKNOWN>")
}

case class Player(id: ClientId, nickname: String)
