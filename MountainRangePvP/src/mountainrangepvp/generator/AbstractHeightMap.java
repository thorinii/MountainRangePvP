/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.generator;

/**
 *
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

    protected abstract int getSample(int where);
}
