/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.terrain;

/**
 *
 * @author lachlan
 */
public class HillsHeightMap extends AbstractHeightMap {

    private static final int SCALE = 5;
    private static final int WALL_WIDTH = 50;
    private static final int WALL_DISTANCE = 3000;
    private static final int WALL_HEIGHT = 110;
    private final int seed;
    private final Noise noise;
    private boolean makeWalls;
    private boolean origin;

    public HillsHeightMap(int seed) {
        this.seed = seed ^ (seed << 2) ^ (seed << 4) ^ (seed << 6) ^ (seed << 8);
        this.noise = new Noise();

        makeWalls = false;
        origin = false;
    }

    @Override
    public int getSample(int x) {
        x = Math.abs(x);

        if (origin && x == 0) {
            return -10000;
        }

        x = x / SCALE;

        if (makeWalls && Math.abs(x % (WALL_DISTANCE / SCALE)) < (WALL_WIDTH / SCALE)) {
            // Wall Generator
            int sample = (int) sample(
                    x / (WALL_WIDTH / SCALE) * (WALL_WIDTH / SCALE));
            return sample + WALL_HEIGHT;
        }

        // Regular terrain
        return (int) (0.2f * sample(x)
                + 0.8f * (sample(x - 1) + sample(x - 2) + sample(x - 3)
                + sample(x + 1) + sample(x + 2) + sample(x + 3)) / 6);

    }

    private float sample(float x) {
        float noise = 50;

        noise += InterpolatedNoise1(x / 800f + 100) * 100000;
        noise += InterpolatedNoise1(x / 70f + 230) * 6000;
        noise += InterpolatedNoise1(x / 300f + 234) * 5000;
        noise += InterpolatedNoise1(x / 70f + 12) * 1000;
        noise += InterpolatedNoise1(x / 50f + 5) * 700;
        noise += InterpolatedNoise1(x / 30f + 32) * 300;
        noise += InterpolatedNoise1(x / 5f + 13) * 100;
        /*
         * noise += InterpolatedNoise1(x / 2f) * 100;
         * noise += InterpolatedNoise1(x / 1f) * 50;
         */

        return noise;
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
