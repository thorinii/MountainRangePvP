package mountainrangepvp.engine.input

import com.badlogic.gdx.{Input, InputProcessor}

/**
 * Sends input to the action mapper.
 */
class StateAccumulatingInputProcessor extends InputProcessor {
  private var mouseButtons = 0
  private var mousePosition: (Float, Float) = (0, 0)


  def keyDown(keycode: Int): Boolean = true

  def keyUp(keycode: Int): Boolean = true

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

  def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = true

  def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    // TODO: convert to game coords
    mousePosition = (screenX, screenY)
    true
  }

  def scrolled(amount: Int): Boolean = true


  def getState = {
    InputState(mousePosition, mouseButtons)
  }
}
