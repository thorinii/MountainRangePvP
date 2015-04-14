/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.terrain;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.terrain.Terrain.Slice;
import org.junit.Test;

import static org.junit.Assert.*;

/**
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

    @Test
    public void testCollideLine() {
        Terrain terrain = new Terrain(new TestGenerator());

        // Test should be collision
        Vector2 p1 = new Vector2(1000, 5);
        Vector2 p2 = new Vector2(1030, 5);

        assertTrue(terrain.collideLine(p1, p2));
        assertTrue(terrain.collideLine(p2, p1));

        // Test should not be collision
        p1 = new Vector2(1000, 14);
        p2 = new Vector2(1030, 14);

        assertFalse(terrain.collideLine(p1, p2));
        assertFalse(terrain.collideLine(p2, p1));
    }

    @Test
    public void testCollideLineNegative() {
        Terrain terrain = new Terrain(new TestGenerator());

        // Test should be collision
        Vector2 p1 = new Vector2(-400, 5);
        Vector2 p2 = new Vector2(-380, 5);

        assertTrue(terrain.collideLine(p1, p2));
        assertTrue(terrain.collideLine(p2, p1));

        // Test should not be collision
        p1 = new Vector2(-400, 14);
        p2 = new Vector2(-380, 14);

        assertFalse(terrain.collideLine(p1, p2));
        assertFalse(terrain.collideLine(p2, p1));
    }

    @Test
    public void testHightestPoint() {
        Terrain terrain = new Terrain(new TestGenerator());

        assertEquals(0, terrain.getHighestPointBetween(0, 5));
        assertEquals(10, terrain.getHighestPointBetween(0, 6));
        assertEquals(11, terrain.getHighestPointBetween(0, 106));
        assertEquals(11, terrain.getHighestPointBetween(105, 106));
        assertEquals(6, terrain.getHighestPointBetween(-196, -100));
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
