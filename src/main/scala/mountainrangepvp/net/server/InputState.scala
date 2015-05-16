package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.InputCommand

/**
 * Computes input state for each frame from an accumulated series of commands.
 */
object InputState {
  def apply(): InputState = new InputState(false, 0, new Vector2(0, 0), 0)

  val FireRateDelay = 0.1f
}


case class InputState(private val fire: Boolean,
                      private val fireTimer: Float,
                      aimDirection: Vector2,
                      private val acc: Int) {
  def accumulate(command: InputCommand): InputState = copy(
    fire = command.fire,
    aimDirection = if (acc == 0) command.aimDirection else aimDirection,
    acc = acc + 1)

  def firing: Boolean = fire && fireTimer > InputState.FireRateDelay

  def nextFrame(dt: Float) = copy(
    fire = false,
    fireTimer = if (firing) 0 else fireTimer + dt,
    aimDirection = aimDirection,
    acc = 0)
}
