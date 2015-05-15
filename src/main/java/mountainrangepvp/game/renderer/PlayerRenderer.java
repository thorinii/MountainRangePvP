package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.ui.TextRenderer;
import mountainrangepvp.game.world.PlayerEntity;
import mountainrangepvp.game.world.Snapshot;
import scala.collection.JavaConversions;

/**
 * @author lachlan
 */
public class PlayerRenderer {
    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 100;

    private final int width, height;
    private final SpriteBatch batch;
    private final TextRenderer textRenderer;
    private final Texture[] bodyTextures;
    private final Texture armsTexture;
    private final Texture spawnBubbleTexture;

    public PlayerRenderer(SpriteBatch batch, TextRenderer textRenderer) {
        this.batch = batch;
        this.textRenderer = textRenderer;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        bodyTextures = new Texture[4];
        bodyTextures[0] = new Texture(Gdx.files.internal(
                "player/body-orange.png"));
        bodyTextures[1] = new Texture(Gdx.files.internal(
                "player/body-red.png"));
        bodyTextures[2] = new Texture(Gdx.files.internal(
                "player/body-green.png"));
        bodyTextures[3] = new Texture(Gdx.files.internal(
                "player/body-blue.png"));

        armsTexture = new Texture(Gdx.files.internal("player/arms.png"));

        spawnBubbleTexture = new Texture(Gdx.files.internal(
                "player/spawn-bubble.png"));
    }

    public void render(Vector2 scroll, Snapshot snapshot) {
        batch.begin();

        for (PlayerEntity player : JavaConversions.asJavaIterable(snapshot.playerEntities())) {
            String nickname = snapshot.nicknameFor(player.player());
            drawPlayer(player, nickname, scroll);
        }

        // TODO: fix this in #40
        if (false) { // !playerManager.getLocalPlayer().isAlive()) {
            drawDeathMessage();
        }

        batch.end();
    }

    private void drawPlayer(PlayerEntity player, String nickname, Vector2 scroll) {
        Texture tex = bodyTextures[0];

        Vector2 pos = player.position().cpy();
        pos.sub(scroll);

        if (pos.x < -PLAYER_WIDTH || pos.x > width) {
            return;
        }
        if (pos.y < -PLAYER_HEIGHT || pos.y > height) {
            return;
        }

        Vector2 dir = new Vector2(1, 0); // TODO: fix in #41; player.getGunDirection();

        if (dir.x < 0) {
            batch.draw(armsTexture,
                       pos.x - 24, pos.y + 38, // Position
                       43, 17, // Origin
                       armsTexture.getWidth(), armsTexture.getHeight(), // Dst WH
                       1, 1, // Scale
                       dir.angle() + 180 % 360, // Rotation
                       0, 0, // Src XY
                       armsTexture.getWidth(), armsTexture.getHeight(), // Src WH
                       false, false); // Flip
        } else {
            batch.draw(armsTexture,
                       pos.x - 24, pos.y + 57, // Position
                       43, 2, // Origin
                       armsTexture.getWidth(), armsTexture.getHeight(), // Dst WH
                       1, 1, // Scale
                       dir.angle() + 180 % 360, // Rotation
                       0, 0, // Src XY
                       armsTexture.getWidth(), armsTexture.getHeight(), // Src WH
                       false, true); // Flip
        }

        batch.draw(tex,
                   pos.x, pos.y, // Position
                   0, 0, // Origin
                   tex.getWidth(), tex.getHeight(), // Dst WH
                   1, 1, // Scale
                   0, // Rotation
                   0, 0, // Src XY
                   tex.getWidth(), tex.getHeight(), // Src WH
                   dir.x > 0, false);

        // TODO: fix in #51
        if (false) { // player.isSpawnBubbleOn()) {
            batch.draw(spawnBubbleTexture,
                       pos.x + PLAYER_WIDTH / 2 - spawnBubbleTexture.getWidth() / 2,
                       pos.y + PLAYER_HEIGHT / 2 - spawnBubbleTexture.
                               getHeight() / 2);
        }

        textRenderer.setSize(20);
        textRenderer.drawString(batch, nickname,
                                (int) pos.x,
                                (int) pos.y + PLAYER_HEIGHT + 20);
    }

    private void drawDeathMessage() {
        textRenderer.setSize(50);
        textRenderer.drawStringCentred(batch, "You were shot",
                                       (int) width / 2,
                                       (int) height / 2);
        textRenderer.setSize(20);
    }
}
