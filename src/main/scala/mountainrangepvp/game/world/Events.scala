package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Event


case class ConnectedEvent(localId: ClientId) extends Event


case class SnapshotEvent(snapshot: Snapshot) extends Event


case class InputCommandEvent(command: InputCommand) extends Event


case object ServerDisconnect extends Event


/**
 * A snapshot of the input system in game terminology.
 * Run can be a decimal value (ie from a joystick), but will likely be ternary.
 *
 * @param run from -1 to 1, where -1 is left, 0 is no movement, and 1 is right.
 * @param jump true if jumping.
 * @param fire the status of the fire button.
 * @param aimDirection the direction of aim.
 */
case class InputCommand(run: Float,
                        jump: Boolean,
                        fire: Boolean, aimDirection: Vector2)
