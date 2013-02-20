/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.Terrain;
import mountainrangepvp.generator.Terrain.Slice;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class MiniMapRenderer implements Renderer {

    private static final Color BACKGROUND = new Color(.5f, .5f, .5f, .3f);
    private static final Color[] playerColours = {
        new Color(1, 1, 0, .5f),
        new Color(1, 0, 0, .5f),
        new Color(0, 1, 0, .5f),
        new Color(0, 0, 1, .5f)
    };
    private static final int WIDTH = 400;
    private static final int HEIGHT = 100;
    private static final float H_SCALE = 20;
    private static final float V_SCALE = 20;
    //
    private final Terrain map;
    private final PlayerManager playerManager;
    //
    private final int width, height;
    //
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    public MiniMapRenderer(SpriteBatch batch, HeightMap map,
            PlayerManager playerManager) {
        this.batch = batch;
        this.map = new Terrain(map);
        this.playerManager = playerManager;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(Vector2 scroll) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        shapeRenderer.setColor(BACKGROUND);
        shapeRenderer.filledRect(width - 10 - WIDTH, height - 10 - HEIGHT,
                                 WIDTH, HEIGHT);

        drawTerrain(scroll);
        shapeRenderer.end();

        drawPlayers(scroll);
    }

    private void drawTerrain(Vector2 scroll) {
        Slice slice = map.getSlice(
                (int) scroll.x + width / 2 - (int) (WIDTH * H_SCALE) / 2,
                (int) (WIDTH * H_SCALE));

        shapeRenderer.setColor(Color.BLACK);

        for (int i = 0; i < WIDTH; i++) {
            float total = 0;
            for (int j = 0; j < H_SCALE; j++) {
                total += slice.get((int) (i * H_SCALE + j));
            }

            float avg = total / H_SCALE;
            avg -= scroll.y;
            avg -= height / 2;
            avg /= V_SCALE;

            avg += HEIGHT / 2;

            if (avg <= 0)
                continue;
            if (avg >= HEIGHT)
                avg = HEIGHT;

            shapeRenderer.filledRect(width - 10 - WIDTH + i,
                                     height - 10 - HEIGHT,
                                     1,
                                     avg);
        }
    }

    private void drawPlayers(Vector2 scroll) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);

        for (Player player : playerManager.getPlayers()) {
            if (!player.isAlive())
                continue;

            Vector2 pos = player.getCentralPosition();
            pos.sub(scroll);

            pos.x -= width / 2;
            pos.y -= height / 2;

            pos.x /= H_SCALE;
            pos.y /= V_SCALE;

            pos.x = Math.min(WIDTH / 2, Math.max(pos.x, -WIDTH / 2));
            pos.y = Math.min(HEIGHT / 2, Math.max(pos.y, -HEIGHT / 2));

            pos.x += width - 10 - WIDTH + WIDTH / 2;
            pos.y += height - 10 - HEIGHT + HEIGHT / 2;

            shapeRenderer.setColor(
                    playerColours[Math.max(0, player.getName().hashCode()
                    % playerColours.length)]);

            shapeRenderer.filledCircle(pos.x, pos.y, 5);
        }

        shapeRenderer.end();
    }
}
