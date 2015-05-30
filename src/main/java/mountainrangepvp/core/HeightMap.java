package mountainrangepvp.core;

/**
 * @author lachlan
 */
public interface HeightMap {

    public int getSample(int x);

    public int[] getBlock(int base, int length);

    public int getSeed();
}
