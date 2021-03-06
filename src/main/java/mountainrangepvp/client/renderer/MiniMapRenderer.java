package mountainrangepvp.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.core.PlayerEntity;
import mountainrangepvp.core.Snapshot;
import mountainrangepvp.core.Terrain;
import mountainrangepvp.core.Terrain.Slice;
import scala.collection.JavaConversions;

/**
 * @author lachlan
 */
public class MiniMapRenderer {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 100;
    private static final int SHIFT = 30;
    private static final float H_SCALE = 20;
    private static final float V_SCALE = 20;

    private final int width, height;

    private final SpriteBatch batch;

    private final Texture background;
    private final Texture border;
    private final Texture terrainGradient;
    private final Texture[] headTextures;

    public MiniMapRenderer(SpriteBatch batch) {
        this.batch = batch;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        background = new Texture(Gdx.files.internal("minimap/background.png"));
        border = new Texture(Gdx.files.internal("minimap/border.png"));
        terrainGradient = new Texture(Gdx.files.internal("minimap/terrain.png"));

        headTextures = new Texture[]{
                new Texture(Gdx.files.internal("minimap/head-orange.png")),
                new Texture(Gdx.files.internal("minimap/head-red.png")),
                new Texture(Gdx.files.internal("minimap/head-green.png")),
                new Texture(Gdx.files.internal("minimap/head-blue.png"))
        };
    }

    public void render(Vector2 scroll, Snapshot snapshot, Terrain terrain) {
        batch.begin();
        batch.draw(background, width - SHIFT - WIDTH, height - SHIFT - HEIGHT,
                   WIDTH, HEIGHT);

        drawTerrain(scroll, terrain);
        drawPlayers(scroll, snapshot);

        batch.draw(border, width - SHIFT - 20 - WIDTH,
                   height - SHIFT - 20 - HEIGHT,
                   WIDTH + 40, HEIGHT + 40);
        batch.end();
    }

    private void drawTerrain(Vector2 scroll, Terrain terrain) {
        Slice slice = terrain.sliceAt(
                (int) scroll.x + width / 2 - (int) (WIDTH * H_SCALE) / 2,
                (int) (WIDTH * H_SCALE));

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

            batch.draw(terrainGradient, width - SHIFT - WIDTH + i,
                       height - SHIFT - HEIGHT,
                       1, avg);
        }
    }

    private void drawPlayers(Vector2 scroll, Snapshot snapshot) {
        for (PlayerEntity player : JavaConversions.asJavaIterable(snapshot.playerEntities())) {
            Vector2 pos = player.position().cpy();
            pos.sub(scroll);

            pos.x -= width / 2;
            pos.y -= height / 2;

            pos.x /= H_SCALE;
            pos.y /= V_SCALE;

            pos.x = Math.min(WIDTH / 2, Math.max(pos.x, -WIDTH / 2));
            pos.y = Math.min(HEIGHT / 2, Math.max(pos.y, -HEIGHT / 2));

            pos.x += width - SHIFT - WIDTH + WIDTH / 2;
            pos.y += height - SHIFT - HEIGHT + HEIGHT / 2;

            Texture head = headTextures[0];

            Vector2 dir = player.aim();
            batch.draw(head,
                       pos.x - head.getWidth() / 2,
                       pos.y - head.getHeight() / 2, // Position
                       0, 0, // Origin
                       head.getWidth(), head.getHeight(), // Dst WH
                       1, 1, // Scale
                       0, // Rotation
                       0, 0, // Src XY
                       head.getWidth(), head.getHeight(), // Src WH
                       dir.x > 0, false);
        }
    }
}
