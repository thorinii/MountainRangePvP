package mountainrangepvp.client;

import mountainrangepvp.engine.SoundRef;

/**
 * Created by lachlan on 15/04/15.
 */
public class Sounds {
    // Eventually parameterise this on gun class
    public static SoundRef GunFire = new SoundRef("shot/fire.ogg");

    public static SoundRef[] SOUNDS = {
            GunFire
    };

    private Sounds() {
        throw new UnsupportedOperationException("Must not be instantiated");
    }
}
