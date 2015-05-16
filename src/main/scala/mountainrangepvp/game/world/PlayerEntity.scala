package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * A player on screen. The thing that runs around shooting things. The thing that can die and <em>be not</em>.
 * Not all players/clients have a PlayerEntity under their name at a given time.
 */
object PlayerEntity {
  val Width = 40
}
case class PlayerEntity(entityId: Long, player: ClientId,
                        position: Vector2, aim: Vector2,
                        velocity: Vector2) {
  // TODO: add gun height vector lazy val
}
