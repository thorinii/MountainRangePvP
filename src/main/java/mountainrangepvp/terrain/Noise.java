package mountainrangepvp.terrain;

/**
 *
 * @author lachlan
 */
public class Noise {

    public float noise(int s) {
        s = (s << 13) ^ s;
        return Math.abs(
                1.0f - ((s * s * (s * s * 15731 + 789221) + 1376312589) & 0x7ffffff) / 1073741824.0f);
    }

    public float noise(float s) {
        return noise((int) s * 10);
    }
}
