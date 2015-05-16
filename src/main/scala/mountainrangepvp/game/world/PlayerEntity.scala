package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * A player on screen. The thing that runs around shooting things. The thing that can die and <em>be not</em>.
 * Not all players/clients have a PlayerEntity under their name at a given time.
 */
case class PlayerEntity(entityId: Long, player: ClientId,
                        position: Vector2, aim: Vector2,
                        velocity: Vector2)
