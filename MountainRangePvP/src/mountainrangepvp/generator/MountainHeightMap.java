/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.generator;

/**
 *
 * @author lachlan
 */
public class MountainHeightMap extends AbstractHeightMap {

    private final int seed;
    private final Noise noise;

    public MountainHeightMap(int seed) {
        this.seed = seed ^ (seed << 2) ^ (seed << 4) ^ (seed << 6) ^ (seed << 8);
        this.noise = new Noise();
    }

    @Override
    protected int getSample(int x) {
        float noise = 50;

        noise += InterpolatedNoise1(x / 300f) * 4000;
        noise += InterpolatedNoise1(x / 30f) * 3000;
        noise += InterpolatedNoise1(x / 15f) * 2000;
        noise += InterpolatedNoise1(x / 10f) * 700;
        noise += InterpolatedNoise1(x / 5f) * 300;
        noise += InterpolatedNoise1(x / 2f) * 100;
        noise += InterpolatedNoise1(x / 1f) * 80;
        noise += InterpolatedNoise1(x / 0.5f) * 20;

        return (int) noise;
    }

    private float InterpolatedNoise1(float x) {
        int integer_X = (int) x;
        float fractional_X = x - integer_X;

        float v1 = SmoothedNoise1(integer_X);
        float v2 = SmoothedNoise1(integer_X + 1);

        return Interpolate(v1, v2, fractional_X) - 0.9f;
    }

    private float Interpolate(float a, float b, float x) {
        return a * (1 - x) + b * x;
    }

    private float SmoothedNoise1(float x) {
        return noise(x) / 2 + noise(x - 1) / 4 + noise(x + 1) / 4;
    }

    private float noise(float x) {
        return noise.noise(x * seed);
    }
}
