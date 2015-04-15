package mountainrangepvp.world.terrain;

/**
 * @author lachlan
 */
public abstract class AbstractHeightMap implements HeightMap {

    @Override
    public int[] getBlock(int base, int length) {
        int[] block = new int[length];

        for (int i = 0; i < length; i++) {
            block[i] = getSample(base + i);
        }

        return block;
    }

    @Override
    public abstract int getSample(int x);
}
