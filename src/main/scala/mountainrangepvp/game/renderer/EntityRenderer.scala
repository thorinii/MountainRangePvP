package mountainrangepvp.game.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.ui.TextRenderer
import mountainrangepvp.game.world.{Entity, PlayerEntity, ShotEntity, Snapshot}

/**
 * Renders Entities by delegating to Player and ShotRenderers.
 */
class EntityRenderer(batch: SpriteBatch, textRenderer: TextRenderer) {
  private val width = Gdx.graphics.getWidth + 1
  private val height = Gdx.graphics.getHeight
  private val playerRenderer: PlayerRenderer = new PlayerRenderer(batch, textRenderer);
  private val shotRenderer: ShotRenderer = new ShotRenderer(batch);

  def render(scroll: Vector2, snapshot: Snapshot, isLocalPlayerAlive: Boolean) = {
    batch.begin()

    for (e <- snapshot.entities)
      renderEntity(snapshot, scroll, e)

    if (!isLocalPlayerAlive) drawDeathMessage()

    batch.end()
  }

  private def renderEntity(snapshot: Snapshot, scroll: Vector2, entity: Entity) = entity match {
    case s: ShotEntity => shotRenderer.renderShot(scroll, s)
    case p: PlayerEntity => playerRenderer.renderPlayer(scroll, p, snapshot.nicknameFor(p.player))
  }

  private def drawDeathMessage() {
    textRenderer.setSize(50)
    textRenderer.drawStringCentred(batch, "You were shot", width / 2, height / 2)
    textRenderer.setSize(20)
  }
}
