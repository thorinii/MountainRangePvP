package mountainrangepvp.engine.input

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Input, InputProcessor}

/**
 * Sends input to the action mapper.
 */
class StateAccumulatingInputProcessor(screenHeight: Int) extends InputProcessor {
  private var mouseButtons = 0
  private val mousePosition = new Vector2(0, 0)
  private var keys: Set[Int] = Set.empty


  def keyDown(keycode: Int): Boolean = {
    keys += keycode
    true
  }

  def keyUp(keycode: Int): Boolean = {
    keys -= keycode
    true
  }

  def keyTyped(character: Char): Boolean = true

  def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    button match {
      case Input.Buttons.LEFT => mouseButtons |= 1
      case Input.Buttons.MIDDLE => mouseButtons |= 2
      case Input.Buttons.RIGHT => mouseButtons |= 4
      case _ =>
    }
    true
  }

  def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    button match {
      case Input.Buttons.LEFT => mouseButtons &= ~1
      case Input.Buttons.MIDDLE => mouseButtons &= ~2
      case Input.Buttons.RIGHT => mouseButtons &= ~4
      case _ =>
    }
    true
  }

  def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
    mousePosition.x = screenX
    mousePosition.y = screenHeight - screenY
    true
  }

  def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    mousePosition.x = screenX
    mousePosition.y = screenHeight - screenY
    true
  }

  def scrolled(amount: Int): Boolean = true


  def getState = {
    InputState(mousePosition, mouseButtons, keys)
  }
}
