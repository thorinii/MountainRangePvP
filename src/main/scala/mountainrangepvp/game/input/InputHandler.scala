package mountainrangepvp.game.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import mountainrangepvp.engine.input.{Bindings, InputMapper, StateAccumulatingInputProcessor}
import mountainrangepvp.engine.util.EventBus
import mountainrangepvp.game.world._

class InputHandler(eventBus: EventBus, screenWidth: Int, screenHeight: Int) {
  private val inputMapper = new InputMapper
  private val inputProcessor = new StateAccumulatingInputProcessor(screenHeight)

  def register() {
    Gdx.input.setInputProcessor(inputProcessor)
  }

  def update(dt: Float): Unit = {
    val rawState = inputProcessor.getState
    val inputState = inputMapper.map(rawState)

    val aim = rawState.mouse.cpy().sub(screenWidth / 2, screenHeight / 2).nor()
    val run = (if (inputState("left")) -1 else 0) + (if (inputState("right")) 1 else 0)

    eventBus.send(InputCommandEvent(
      InputCommand(run = run,
                   jump = inputState("jump"),
                   fire = inputState("fire"),
                   aimDirection = aim)))
  }


  inputMapper.addState("fire")
  inputMapper.bindMouseButton(Bindings.MouseLeft, "fire")

  inputMapper.addState("left")
  inputMapper.bindKeyboard(Keys.LEFT, "left")
  inputMapper.bindKeyboard(Keys.A, "left")

  inputMapper.addState("right")
  inputMapper.bindKeyboard(Keys.RIGHT, "right")
  inputMapper.bindKeyboard(Keys.D, "right")

  inputMapper.addState("jump")
  inputMapper.bindKeyboard(Keys.UP, "jump")
  inputMapper.bindKeyboard(Keys.W, "jump")
  inputMapper.bindKeyboard(Keys.SPACE, "jump")
}
