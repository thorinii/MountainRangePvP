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


  def addShot(playerId: ClientId, direction: Vector2): Snapshot =
    addShot(playerId,
            playerEntities.find(_.player == playerId).map(_.position).getOrElse(new Vector2(0, 0)),
            direction)

  def addShot(playerId: ClientId, base: Vector2, direction: Vector2): Snapshot =
    copy(shots = shots + Shot(playerId, base, direction, 0f))


  def addPlayerEntity(entityId: Long, playerId: ClientId, position: Vector2) =
    copy(playerEntities = playerEntities + PlayerEntity(entityId, playerId,
                                                        position, new Vector2(0, 0),
                                                        new Vector2(1+Math.random().toFloat*60, 30)))

  def removePlayerEntity(playerId: ClientId) =
    copy(playerEntities = playerEntities.filterNot(_.player == playerId))

  def getPlayerEntity(playerId: ClientId) = playerEntities.find(_.player == playerId)


  def nicknameFor(playerId: ClientId) = players.find(_.id == playerId).map(_.nickname).getOrElse("<UNKNOWN>")


  def aim(playerId: ClientId, direction: Vector2) =
    copy(playerEntities = playerEntities.map(e => if (e.player == playerId) e.copy(aim = direction) else e))
}

case class Player(id: ClientId, nickname: String)
