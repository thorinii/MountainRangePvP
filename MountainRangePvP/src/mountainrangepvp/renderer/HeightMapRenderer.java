/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
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
    private final Map<Integer, int[]> blocks;

    public HeightMapRenderer(HeightMap map) {
        this.map = map;

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        blocks = new HashMap<>();
    }

    public void render(int scroll) {
        int[] block1 = getBlock(scroll / width);
        int[] block2 = getBlock(scroll / width + 1);

        for(int i = 0; i < width; i++){
            
        }
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
