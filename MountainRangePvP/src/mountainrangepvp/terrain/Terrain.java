/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.terrain;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lachlan
 */
public class Terrain {

    private static final int BLOCK_SIZE = 1024;
    private final HeightMap heightMap;
    private final Map<Integer, int[]> blocks;

    public Terrain(HeightMap heightMap) {
        this.heightMap = heightMap;
        blocks = new HashMap<>();
    }

    public Slice getSlice(int base, int size) {
        return new Slice(base, size);
    }

    public int getSample(int where) {
        return heightMap.getSample(where);
    }

    private int[] getBlock(int blockNumber) {
        if (blocks.containsKey(blockNumber)) {
            return blocks.get(blockNumber);
        } else {
            int[] block = heightMap.getBlock(BLOCK_SIZE * blockNumber,
                                             BLOCK_SIZE);

            blocks.put(blockNumber, block);

            return block;
        }
    }

    public class Slice {

        final int base;
        final int size;
        final int offset;
        final int[][] blocks;

        public Slice(int base, int size) {
            this.base = base;
            this.size = size;
            this.offset = base % BLOCK_SIZE + ((base >= 0) ? 0 : BLOCK_SIZE);

            int block = base / BLOCK_SIZE;
            if (base < 0)
                block -= 1;

            blocks = new int[size / BLOCK_SIZE + 2][];

            for (int i = 0; i < size / BLOCK_SIZE + 2; i++) {
                blocks[i] = getBlock(block + i);
            }
        }

        public int get(int where) {
            if (where < 0 || where >= size)
                throw new IllegalArgumentException(
                        "Out of range of slice: " + where);

            int blockN = (where + offset) / BLOCK_SIZE;
            int[] block = blocks[blockN];

            return block[(where + offset) % BLOCK_SIZE];
        }
    }
}
