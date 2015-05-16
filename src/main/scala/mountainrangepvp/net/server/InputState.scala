package mountainrangepvp.net.server

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.game.world.InputCommand

/**
 * Computes input state for each frame from an accumulated series of commands.
 */
object InputState {
  def apply(): InputState = new InputState(0,
                                           false,
                                           false, 0,
                                           new Vector2(0, 0),
                                           0)

  val FireRateDelay = 0.1f
}


case class InputState(private val runRaw: Float,
                      jump: Boolean,
                      private val fire: Boolean,
                      private val fireTimer: Float,
                      aimDirection: Vector2,
                      private val acc: Int) {
  def accumulate(command: InputCommand): InputState =
    InputState(runRaw + command.run,
               jump = command.jump,
               fire = command.fire,
               fireTimer,
               if (acc == 0) command.aimDirection else aimDirection,
               acc + 1)

  def run = runRaw.max(-1f).min(1f)

  def firing: Boolean = fire && fireTimer > InputState.FireRateDelay

  def nextFrame(dt: Float) =
    InputState(0,
               jump = false,
               fire = false,
               if (firing) 0 else fireTimer + dt,
               aimDirection,
               0)
}
