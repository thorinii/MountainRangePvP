package mountainrangepvp.game.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.input.{ActionMapper, StateAccumulatingInputProcessor, Bindings, DelayedRepeatingAction}
import mountainrangepvp.engine.util.EventBus
import mountainrangepvp.game.world._

class InputHandler(eventbus: EventBus) {
  private val actionMapper = new ActionMapper
  private val inputProcessor = new StateAccumulatingInputProcessor()


  actionMapper.addAction("fire", new DelayedRepeatingAction(state => {
    eventbus.send(new PlayerFiredEvent(new Vector2(0, 1)))
    0.1f
  }))

  actionMapper.bindMouseButton(Bindings.MouseLeft, "fire")


  def register() {
    Gdx.input.setInputProcessor(inputProcessor)
  }

  def update(dt: Float): Unit = {
    val state = inputProcessor.getState
    actionMapper.update(state, dt)
  }

  private def aimAtCrosshair(player: Player) {
    val x: Int = Gdx.input.getX
    val y: Int = Gdx.graphics.getHeight - Gdx.input.getY
    val target: Vector2 = new Vector2(x, y)
    target.x -= Gdx.graphics.getWidth / 2
    target.y -= Gdx.graphics.getHeight / 2
    target.nor
    val dir: Vector2 = player.getGunDirection
    val lerpSpeed: Float = Math.min(0.8f, Math.max(0.3f, 10f / player.getVelocity.x))
    dir.lerp(target, lerpSpeed)
  }

  private def doShooting(player: Player) {
    val pos: Vector2 = player.getCentralPosition
    eventbus.send(new PlayerFiredEvent(player.getGunDirection.cpy))
    val kickback: Vector2 = player.getGunDirection.cpy.scl(-90f)
    player.getVelocity.add(kickback)
  }
}
