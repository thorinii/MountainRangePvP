/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class LeaderboardRenderer implements Renderer {

    private final PlayerManager playerManager;
    private final SpriteBatch batch;
    private final TextRenderer textRenderer;
    //
    private final Texture[] bodyTextures;
    //
    private final int width, height;

    public LeaderboardRenderer(SpriteBatch batch, PlayerManager playerManager,
            TextRenderer textRenderer) {
        this.batch = batch;
        this.playerManager = playerManager;
        this.textRenderer = textRenderer;

        bodyTextures = new Texture[]{
            new Texture(Gdx.files.internal("player/head-orange.png")),
            new Texture(Gdx.files.internal("player/head-red.png")),
            new Texture(Gdx.files.internal("player/head-green.png")),
            new Texture(Gdx.files.internal("player/head-blue.png"))
        };

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();
    }

    @Override
    public void render(Vector2 scroll) {
        List<Player> topPlayers = playerManager.getPlayersByHits(3);

        int x = 20;
        int y = height - 20;

        batch.begin();
        for (Player p : topPlayers) {
            if (p.getHits() == 0)
                continue;

            Texture tex = bodyTextures[
                    Math.max(0, p.getName().hashCode()
                    % bodyTextures.length)];

            String text = p.getName() + " @ " + p.getHits();
            textRenderer.drawString(text, batch, x + tex.getWidth() + 20, y);


            batch.draw(tex, x, y - tex.getWidth() * 5 / 6);

            y -= 40;
        }
        batch.end();
    }
}
