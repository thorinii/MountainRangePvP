/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.terrain;

import com.badlogic.gdx.math.Vector2;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author lachlan
 */
public class Terrain {

    private static final int MAX_CACHED_BLOCKS = 100;
    private static final int BLOCK_SIZE = 1024;
    private final HeightMap heightMap;
    private final Map<Integer, int[]> blocks;

    public Terrain(HeightMap heightMap) {
        this.heightMap = heightMap;
        blocks = new LinkedHashMap<Integer, int[]>() {
            @Override
            protected boolean removeEldestEntry(Entry<Integer, int[]> eldest) {
                return size() > MAX_CACHED_BLOCKS;
            }
        };
    }

    public Slice getSlice(int base, int size) {
        return new Slice(base, size);
    }

    public Slice getSliceBetween(int start, int end) {
        return new Slice(start, end - start);
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

    public boolean collideLine(Vector2 p1, Vector2 p2) {
        if (p1.x > p2.x) {
            Vector2 tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        float width = (float) Math.ceil(p2.x - p1.x);
        if (width == 0)
            width = 1;

        Slice slice = getSlice((int) p1.x, (int) width);

        for (int i = 0; i < width; i++) {
            if (slice.get(i) >= (width - i) / width * p1.y + i / width * p2.y) {
                return true;
            }
        }

        return false;
    }

    public int getHighestPointBetween(int start, int end) {
        return getSliceBetween(start, end).getHighestPoint();
    }

    public class Slice {

        final int base;
        final int size;
        final int offset;
        final int[][] blocks;

        public Slice(int base, int size) {
            if (size <= 0)
                throw new IllegalArgumentException(
                        "Size must be greater than zero: " + size);

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
            if (where < 0)
                throw new IllegalArgumentException(
                        "Below range of slice: " + where);
            if (where >= size)
                throw new IllegalArgumentException(
                        "Above range of slice: " + where);

            int blockN = (where + offset) / BLOCK_SIZE;
            int[] block = blocks[blockN];

            return block[(where + offset) % BLOCK_SIZE];
        }

        public int getHighestPoint() {
            int highest = get(0);

            for (int i = 1; i < size; i++) {
                int p = get(i);
                if (p > highest)
                    highest = p;
            }

            return highest;
        }

        public int getHighestIndex() {
            int highest = get(0);
            int index = 0;

            for (int i = 1; i < size; i++) {
                int p = get(i);
                if (p > highest) {
                    highest = p;
                    index = i;
                }
            }

            return index;
        }

        public int getLowestPoint() {
            int lowest = get(0);

            for (int i = 1; i < size; i++) {
                int p = get(i);
                if (p < lowest)
                    lowest = p;
            }

            return lowest;
        }

        public int getLowestIndex() {
            int lowest = get(0);
            int index = 0;

            for (int i = 1; i < size; i++) {
                int p = get(i);
                if (p < lowest) {
                    lowest = p;
                    index = i;
                }
            }

            return index;
        }
    }
}
