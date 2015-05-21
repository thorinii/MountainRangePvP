package mountainrangepvp.game.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.ui.TextRenderer
import mountainrangepvp.game.world.Snapshot

/**
 * Renders Entities by delegating to Player and ShotRenderers.
 */
class EntityRenderer(batch: SpriteBatch, textRenderer: TextRenderer) {
  private val playerRenderer: PlayerRenderer = new PlayerRenderer(batch, textRenderer);
  private val shotRenderer: ShotRenderer = new ShotRenderer(batch);

  def render(scroll: Vector2, snapshot: Snapshot, localPlayerIsAlive: Boolean) = {
    shotRenderer.render(scroll, snapshot)
    playerRenderer.render(scroll, snapshot, localPlayerIsAlive)
  }
}
