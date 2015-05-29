package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mountainrangepvp.engine.ui.TextRenderer;
import mountainrangepvp.game.world.ClientId;
import mountainrangepvp.game.world.LeaderBoard;
import mountainrangepvp.game.world.Snapshot;
import scala.collection.JavaConversions;

/**
 * @author lachlan
 */
public class LeaderBoardRenderer {

    private final SpriteBatch batch;
    private final TextRenderer textRenderer;

    private final Texture[] bodyTextures;

    private final int width, height;

    public LeaderBoardRenderer(SpriteBatch batch, TextRenderer textRenderer) {
        this.batch = batch;
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

    public void render(Snapshot snapshot) {
        int x = 20;
        int y = height - 20;

        batch.begin();
        for (LeaderBoard.Stats stats : JavaConversions.asJavaIterable(snapshot.leaderBoard().sortedByHighest().take(4))) {
            ClientId player = stats.player();
            if (stats.hits() == 0)
                continue;

            Texture tex = bodyTextures[0];
            batch.draw(tex, x, y - tex.getWidth() * 5 / 6);

            String text = snapshot.nicknameFor(player) + " " + stats.hits() + "/" + stats.deaths();
            textRenderer.drawString(batch, text, x + tex.getWidth() + 20, y);

            y -= 40;
        }
        batch.end();
    }
}
