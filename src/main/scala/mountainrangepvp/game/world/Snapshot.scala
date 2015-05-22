package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * The game state at a point in time.
 */
object Snapshot {
  def empty(seed: Int, teamsOn: Boolean) = Snapshot(seed, teamsOn,
                                                    Set.empty, Set.empty)
}

case class Snapshot(seed: Int,
                    teamsOn: Boolean,
                    players: Set[Player],
                    entities: Set[Entity]) {

  lazy val playerEntities = entities.filter(_.isInstanceOf[PlayerEntity]).map(_.asInstanceOf[PlayerEntity])

  def join(playerId: ClientId, nickname: String) = copy(players = players + Player(playerId, nickname))

  def leave(playerId: ClientId) = copy(players = players.filterNot(_.id == playerId))


  def addShot(entityId: Long, playerId: ClientId, direction: Vector2): Snapshot =
    addShot(entityId, playerId,
            getPlayerEntity(playerId).map(_.position).getOrElse(Vector2.Zero).cpy().add(0, PlayerEntity.GunHeight),
            direction)

  def addShot(entityId: Long, playerId: ClientId, base: Vector2, direction: Vector2): Snapshot =
    copy(entities = entities + ShotEntity(entityId, playerId,
                                          base, direction.cpy().scl(ShotEntity.Speed),
                                          false, 0f))


  def addPlayerEntity(entityId: Long, playerId: ClientId, position: Vector2) =
    copy(entities = entities + PlayerEntity(entityId, playerId,
                                            position, new Vector2(0, 0),
                                            new Vector2(0, 0), false))

  def removePlayerEntity(playerId: ClientId) =
    copy(entities = entities.filterNot {
      case PlayerEntity(_, pid, _, _, _, _) => pid == playerId
      case _ => false
    })

  def getPlayerEntity(playerId: ClientId) = entities.find {
    case PlayerEntity(_, pid, _, _, _, _) => pid == playerId
    case _ => false
  }.map(_.asInstanceOf[PlayerEntity])


  def nicknameFor(playerId: ClientId) = players.find(_.id == playerId).map(_.nickname).getOrElse("<UNKNOWN>")


  def updatePlayer(playerId: ClientId, f: PlayerEntity => PlayerEntity) =
    copy(entities = entities.map {
      case p@PlayerEntity(_, pid, _, _, _, _) if (pid == playerId) => f(p)
      case e => e
    })
}

case class Player(id: ClientId, nickname: String)
