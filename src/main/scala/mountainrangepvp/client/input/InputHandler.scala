package mountainrangepvp.client.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.input.{Bindings, InputMapper, InputState, StateAccumulatingInputProcessor}
import mountainrangepvp.engine.util.EventBus
import mountainrangepvp.core._

class InputHandler(eventBus: EventBus, screenWidth: Int, screenHeight: Int) {
  private val inputMapper = new InputMapper
  private val inputProcessor = new StateAccumulatingInputProcessor(screenHeight)

  def register() {
    Gdx.input.setInputProcessor(inputProcessor)
  }

  def update(dt: Float, camera: Vector2): Unit = {
    val rawState = inputProcessor.getState
    val inputState = inputMapper.map(rawState)

    val aim = crosshairRelativeToPlayer(camera, rawState)
    val run = (if (inputState("left")) -1 else 0) + (if (inputState("right")) 1 else 0)

    eventBus.send(InputCommandEvent(
      InputCommand(run = run,
                   jump = inputState("jump"),
                   fire = inputState("fire"),
                   aimDirection = aim)))
  }

  def crosshairRelativeToPlayer(camera: Vector2, rawState: InputState): Vector2 =
    rawState.mouse.cpy()
    .sub(screenWidth / 2, screenHeight / 2)
    .add(camera)


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
