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
import java.util.HashMap;
import java.util.Map;
import mountainrangepvp.generator.HeightMap;

/**
 *
 * @author lachlan
 */
public class HeightMapRenderer implements Renderer {

    private static final Color BASE_COLOUR = new Color(0.5765f, 0.5843f, 0.5922f,
                                                       1);
    //
    private final HeightMap map;
    private final int width, height;
    //
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Texture worldSliceTexture;
    //
    private final Map<Integer, int[]> blocks;

    public HeightMapRenderer(SpriteBatch batch, HeightMap map) {
        this.map = map;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
        this.batch = batch;

        worldSliceTexture = new Texture(Gdx.files.internal("terrain/slice2.png"));

        blocks = new HashMap<>();
    }

    public void render(Vector2 scroll) {
        /*
         * Calculate where to grab the block data from.
         * There can only be 2 blocks max onscreen, due to the width of each
         * being the screen. Note there is 1 case where only the first block
         * will be shown.
         */
        int offset = ((int) scroll.x) % width;
        int[] block1, block2;

        if (offset < 0) {
            // If it's negative, we need to move over 1 block
            offset += width;

            block1 = getBlock((int) scroll.x / width - 1);
            block2 = getBlock((int) scroll.x / width);
        } else {
            block1 = getBlock((int) scroll.x / width);
            block2 = getBlock((int) scroll.x / width + 1);
        }

        /*
         * Render the block(s)
         */
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(BASE_COLOUR);
        for (int i = 0; i < width; i++) {
            int column;
            if (i + offset >= width) {
                column = block2[i + offset - width];
            } else {
                column = block1[i + offset];
            }

            column -= (int) scroll.y;

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
            int column;
            if (i + offset >= width) {
                column = block2[i + offset - width];
            } else {
                column = block1[i + offset];
            }

            column -= (int) scroll.y;

            if (column <= 0) {
                continue;
            } else {
                column = Math.min(column, height);
            }

            batch.draw(worldSliceTexture, i - 1, column - worldSliceTexture.
                    getHeight());
        }
        batch.end();
    }

    /**
     * Internal caching system. Eventually will be moved elsewhere.
     * <p/>
     * @param blockNumber
     * @return
     */
    private int[] getBlock(int blockNumber) {
        if (blocks.containsKey(blockNumber)) {
            return blocks.get(blockNumber);
        } else {
            int[] block = map.getBlock(width * blockNumber, width);

            blocks.put(blockNumber, block);

            return block;
        }
    }
}
