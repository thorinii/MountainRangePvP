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
public class HeightMapRenderer {

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
        int[] block1 = getBlock(scrollX / width);
        int[] block2 = getBlock(scrollX / width + 1);

        int offset = Math.abs(scrollX % width);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);

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

            shapeRenderer.line(i, 0, i, column);
        }

        shapeRenderer.end();
    }

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
