package mountainrangepvp.game.input

import com.badlogic.gdx.Gdx
import mountainrangepvp.engine.input.{Bindings, InputMapper, StateAccumulatingInputProcessor}
import mountainrangepvp.engine.util.EventBus
import mountainrangepvp.game.world._

class InputHandler(eventBus: EventBus, screenWidth: Int, screenHeight: Int) {
  private val inputMapper = new InputMapper
  private val inputProcessor = new StateAccumulatingInputProcessor(screenHeight)

  inputMapper.addState("fire")
  inputMapper.bindMouseButton(Bindings.MouseLeft, "fire")


  def register() {
    Gdx.input.setInputProcessor(inputProcessor)
  }

  def update(dt: Float): Unit = {
    val rawState = inputProcessor.getState
    val inputState = inputMapper.map(rawState)

    val aim = rawState.mouse.cpy().sub(screenWidth / 2, screenHeight / 2).nor()

    eventBus.send(InputCommandEvent(
      InputCommand(fire = inputState("fire"),
                   aimDirection = aim)))
  }
}
