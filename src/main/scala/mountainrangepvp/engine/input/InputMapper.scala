package mountainrangepvp.engine.input

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.input.Bindings._

/**
 * Maps from raw key and mouse events to actions.
 */
class InputMapper {
  private var emptyStates: Map[String, Boolean] = Map.empty
  private var mouseButtons: Map[MouseButton, String] = Map.empty
  private var keys: Map[Int, String] = Map.empty

  def addState(name: String) = {
    emptyStates += name -> false
  }

  def bindMouseButton(button: MouseButton, action: String) = {
    mouseButtons += (button -> action)
  }

  def bindKeyboard(key: Int, action: String) = {
    keys += (key -> action)
  }

  def map(state: InputState) = {
    var mapped = emptyStates

    List(MouseLeft, MouseMiddle, MouseRight).foreach { b =>
      if (state.buttonDown(b)) {
        mouseButtons.get(b).foreach(state => mapped += state -> true)
      }
    }

    state.keys.flatMap(keys.get).foreach(mapped += _ -> true)

    mapped
  }
}


/**
 * The current state of the input system.
 */
case class InputState(mouse: Vector2, mouseButtons: Int, keys: Set[Int]) {
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
