package mountainrangepvp.engine.input

/**
 * A response to input.
 */
trait Action {
  def update(dt: Float): Unit = {}

  def fire(state: InputState): Unit
}


class DelayedRepeatingAction(handler: InputState => Float) extends Action {
  private var delay: Float = 0
  private var timer: Float = 0

  override def update(dt: Float): Unit = {
    timer += dt
  }

  override def fire(state: InputState) = {
    if (delay == 0 || timer >= delay) {
      timer = 0
      delay = handler(state)
    }
  }
}
