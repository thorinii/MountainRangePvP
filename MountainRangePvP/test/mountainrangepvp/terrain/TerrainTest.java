/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.terrain;

import mountainrangepvp.terrain.AbstractHeightMap;
import mountainrangepvp.terrain.Terrain.Slice;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lachlan
 */
public class TerrainTest {

    @Test
    public void testPositiveOriginSlice() {
        Terrain terrain = new Terrain(new TestGenerator());
        Slice result = terrain.getSlice(0, 200);

        assertEquals(10, result.get(5));
        assertEquals(11, result.get(105));
    }

    @Test
    public void testPositiveBlock2Slice() {
        Terrain terrain = new Terrain(new TestGenerator());
        Slice result = terrain.getSlice(1024, 200);

        assertEquals(12, result.get(5));
        assertEquals(13, result.get(105));
    }

    @Test
    public void testNegativeOriginSlice() {
        Terrain terrain = new Terrain(new TestGenerator());
        Slice result = terrain.getSlice(-200, 200);

        assertEquals(6, result.get(5));
        assertEquals(7, result.get(105));
    }

    @Test
    public void testNegativeBlock2Slice() {
        Terrain terrain = new Terrain(new TestGenerator());
        Slice result = terrain.getSlice(-400, 200);

        assertEquals(8, result.get(5));
        assertEquals(9, result.get(105));
    }

    private class TestGenerator extends AbstractHeightMap {

        @Override
        public int getSample(int x) {
            switch (x) {
                case -195:
                    return 6;
                case -95:
                    return 7;
                case -395:
                    return 8;
                case -295:
                    return 9;

                case 5:
                    return 10;
                case 105:
                    return 11;
                case 1029:
                    return 12;
                case 1129:
                    return 13;

                default:
                    return 0;
            }
        }
    }
}
