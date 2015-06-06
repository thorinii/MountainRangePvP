package mountainrangepvp.core

import com.badlogic.gdx.math.Vector2

/**
 * The game state at a point in time.
 */
object Snapshot {
  def empty(seed: Int, teamsOn: Boolean) = Snapshot(seed, teamsOn,
                                                    Set.empty, LeaderBoard(),
                                                    Set.empty,
                                                    List.empty)
}

case class Snapshot(seed: Int,
                    teamsOn: Boolean,
                    players: Set[Player], leaderBoard: LeaderBoard,
                    entities: Set[Entity],
                    respawnTimers: List[RespawnTimer]) {

  lazy val playerEntities = entities.filter(_.isInstanceOf[PlayerEntity]).map(_.asInstanceOf[PlayerEntity])

  def join(playerId: ClientId, nickname: String) =
    copy(players = players + Player(playerId, nickname), leaderBoard = leaderBoard.join(playerId))

  def leave(playerId: ClientId) =
    copy(players = players.filterNot(_.id == playerId), leaderBoard = leaderBoard.leave(playerId))


  def addRespawnTimer(player: ClientId, timeToRespawn: Float) = {
    if (respawnTimers.exists(_.player == player))
      this
    else
      copy(respawnTimers = RespawnTimer(player, timeToRespawn) :: respawnTimers)
  }


  def addShot(entityId: Long, playerId: ClientId, direction: Vector2): Snapshot =
    addShot(entityId, playerId,
            getPlayerEntity(playerId).map(_.position).getOrElse(Vector2.Zero).cpy().add(0, PlayerEntity.GunHeight),
            direction)

  def addShot(entityId: Long, playerId: ClientId, base: Vector2, direction: Vector2): Snapshot =
    copy(entities = entities + ShotEntity(entityId, playerId, base, direction))


  def addPlayerEntity(entityId: Long, playerId: ClientId, position: Vector2) =
    copy(entities = entities + PlayerEntity(entityId, playerId, position))

  def removePlayerEntity(playerId: ClientId) =
    copy(entities = entities.filterNot {
                                         case p: PlayerEntity => p.player == playerId
                                         case _ => false
                                       })

  def getPlayerEntity(playerId: ClientId) = entities.find {
                                                            case p: PlayerEntity => p.player == playerId
                                                            case _ => false
                                                          }.map(_.asInstanceOf[PlayerEntity])


  def nicknameFor(playerId: ClientId) = players.find(_.id == playerId).map(_.nickname).getOrElse("<UNKNOWN>")


  def updatePlayer(playerId: ClientId, f: PlayerEntity => PlayerEntity) =
    copy(entities = entities.map {
                                   case p: PlayerEntity if p.player == playerId => f(p)
                                   case e => e
                                 })


  def tickTimers(dt: Float) =
    copy(respawnTimers = respawnTimers.map(_.tick(dt)))

  def removeExpiredTimers =
    copy(respawnTimers = respawnTimers.filterNot(_.expired))


  def headlessPlayers = players.filterNot(player => entities.exists {
                                                                      case p: PlayerEntity if p.player == player.id => true
                                                                      case _ => false
                                                                    })
}

case class Player(id: ClientId, nickname: String)


case class RespawnTimer(player: ClientId, timeLeft: Float) {
  def tick(dt: Float) = RespawnTimer(player, timeLeft - dt)

  def expired = timeLeft <= 0
}
