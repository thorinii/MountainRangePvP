package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.InputCommand

/**
 * Computes input state for each frame from an accumulated series of commands.
 */
object InputState {
  def apply(): InputState = new InputState(false, new Vector2(0, 0), 0)
}


case class InputState(fire: Boolean, aimDirection: Vector2, acc: Int) {
  def accumulate(command: InputCommand): InputState = {
    new InputState(command.fire,
                   if (acc == 0) command.aimDirection else aimDirection,
                   acc + 1)
  }

  def nextFrame = new InputState(false, aimDirection, 0)
}
