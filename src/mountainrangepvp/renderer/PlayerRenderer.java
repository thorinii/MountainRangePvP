/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class PlayerRenderer implements Renderer {

    private final PlayerManager playerManager;
    private final int width, height;
    private final SpriteBatch batch;
    private final TextRenderer textRenderer;
    private final Texture[] bodyTextures;
    private final Texture armsTexture;
    private final Texture spawnBubbleTexture;

    public PlayerRenderer(SpriteBatch batch, TextRenderer textRenderer,
            PlayerManager playerManager) {
        this.batch = batch;
        this.textRenderer = textRenderer;
        this.playerManager = playerManager;

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

    @Override
    public void render(Vector2 scroll) {
        batch.begin();

        for (Player player : playerManager.getPlayers()) {
            if (player.isAlive()) {
                drawPlayer(player, scroll);
            }
        }

        batch.end();
    }

    private void drawPlayer(Player player, Vector2 scroll) {
        Texture tex = bodyTextures[Math.max(0,
                                            player.getName().hashCode() % bodyTextures.length)];

        Vector2 pos = player.getPosition().cpy();
        pos.sub(scroll);

        if (pos.x < -Player.WIDTH || pos.x > width) {
            return;
        }
        if (pos.y < -Player.HEIGHT || pos.y > height) {
            return;
        }

        Vector2 ppos = player.getPosition().cpy();
        ppos.x += Player.WIDTH / 2;
        ppos.y += 60;
        Vector2 dir = player.getGunDirection();

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

        if (player.isSpawnBubbleOn()) {
            batch.draw(spawnBubbleTexture,
                       pos.x + Player.WIDTH / 2 - spawnBubbleTexture.getWidth() / 2,
                       pos.y + Player.HEIGHT / 2 - spawnBubbleTexture.getHeight() / 2);
        }

        textRenderer.drawString(player.getName(), batch,
                                (int) pos.x,
                                (int) pos.y + Player.HEIGHT + 20);
    }
}
