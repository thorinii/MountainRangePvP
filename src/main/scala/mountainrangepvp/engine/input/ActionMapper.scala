package mountainrangepvp.engine.input

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.input.Bindings._

/**
 * Maps from raw key and mouse events to actions.
 */
class ActionMapper {
  private var actions: Map[String, Action] = Map.empty
  private var mouseButtons: Map[MouseButton, String] = Map.empty

  def addAction(name: String, action: Action) = {
    actions += (name -> action)
  }

  def bindMouseButton(button: MouseButton, action: String) = {
    mouseButtons += (button -> action)
  }

  def update(state: InputState, dt: Float) = {
    actions.values.foreach(_.update(dt))

    List(MouseLeft, MouseMiddle, MouseRight).foreach { b =>
      if (state.buttonDown(b)) {
        mouseButtons.get(b).map(actions.apply).foreach(_.fire(state))
      }
    }
  }
}


/**
 * The current state of the input system.
 */
case class InputState(mouse: Vector2, mouseButtons: Int) {
  def buttonDown(b: Bindings.MouseButton) = b match {
    case MouseLeft => (mouseButtons & 1) != 0
    case MouseMiddle => (mouseButtons & 2) != 0
    case MouseRight => (mouseButtons & 4) != 0
  }
}


object Bindings {

  sealed trait MouseButton

  case object MouseLeft extends MouseButton

  case object MouseMiddle extends MouseButton

  case object MouseRight extends MouseButton

}
