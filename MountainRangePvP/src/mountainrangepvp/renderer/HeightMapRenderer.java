/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.HashMap;
import java.util.Map;
import mountainrangepvp.generator.HeightMap;

/**
 *
 * @author lachlan
 */
public class HeightMapRenderer implements Renderer {

    private static final boolean DEBUG = false;
    //
    private final HeightMap map;
    private final int width, height;
    private final ShapeRenderer shapeRenderer;
    private final Map<Integer, int[]> blocks;

    public HeightMapRenderer(HeightMap map) {
        this.map = map;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();

        blocks = new HashMap<>();
    }

    public void render(int scrollX, int scrollY) {
        /*
         * Calculate where to grab the block data from.
         * There can only be 2 blocks max onscreen, due to the width of each
         * being the screen. Note there is 1 case where only the first block
         * will be shown.
         */
        int offset = scrollX % width;
        int[] block1, block2;

        if (offset < 0) {
            // If it's negative, we need to move over 1 block
            offset += width;

            block1 = getBlock(scrollX / width - 1);
            block2 = getBlock(scrollX / width);
        } else {
            block1 = getBlock(scrollX / width);
            block2 = getBlock(scrollX / width + 1);
        }

        /*
         * Render the block(s)
         */
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < width; i++) {
            int column;
            if (i + offset >= width) {
                column = block2[i + offset - width];
            } else {
                column = block1[i + offset];
            }

            column -= scrollY;

            if (column <= 0) {
                continue;
            } else {
                column = Math.min(column, height);
            }

            // If debug, render block boundaries
            if (DEBUG && (i + offset) % width == 0) {
                shapeRenderer.setColor(1, 1, 0, 1);
            } else {
                shapeRenderer.setColor(0, 0, 0, 1);
            }
            shapeRenderer.line(i, 0, i, column);
        }
        shapeRenderer.end();
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
