package mountainrangepvp.world.terrain;

/**
 * @author lachlan
 */
public interface HeightMap {

    public int getSample(int x);

    public int[] getBlock(int base, int length);
}
