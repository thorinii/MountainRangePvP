package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.world.Terrain;

/**
 * @author lachlan
 */
public class TerrainRenderer {

    private static final Color BASE_COLOUR = new Color(0.5451f, 0.3686f, 0.2314f,
                                                       1);

    private final int width, height;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Texture worldSliceTexture;

    public TerrainRenderer(SpriteBatch batch) {
        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
        this.batch = batch;

        worldSliceTexture = new Texture(Gdx.files.internal("terrain/slice.png"));
    }

    public void render(Vector2 scroll, Terrain map) {
        Terrain.Slice slice = map.getSlice((int) scroll.x, width);

        /*
         * Render the block(s)
         */
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(BASE_COLOUR);
        for (int i = 0; i < width; i++) {
            int column = slice.get(i) - (int) scroll.y;

            if (column <= 0) {
                continue;
            } else {
                column = Math.min(column, height);
            }

            shapeRenderer.line(i, 0, i, column);
        }
        shapeRenderer.end();

        batch.begin();
        for (int i = 0; i < width; i++) {
            int column = slice.get(i) - (int) scroll.y;

            if (column <= 0) {
                continue;
            }

            batch.draw(worldSliceTexture, i - 1, column - worldSliceTexture.
                    getHeight());
        }
        batch.end();
    }
//    /**
//     * Internal caching system. Eventually will be moved elsewhere.
//     * <p/>
//     * @param blockNumber
//     * @return
//     */
//    private int[] getBlock(int blockNumber) {
//        if (blocks.containsKey(blockNumber)) {
//            return blocks.get(blockNumber);
//        } else {
//            int[] block = map.getBlock(width * blockNumber, width);
//
//            blocks.put(blockNumber, block);
//
//            return block;
//        }
//    }
}
