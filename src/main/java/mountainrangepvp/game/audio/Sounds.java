package mountainrangepvp.game.audio;

/**
 * Created by lachlan on 15/04/15.
 */
public class Sounds {
    // Eventually parameterise this on gun class
    public static SoundRef GunFire = new SoundRef("shot/fire.ogg");

    public static SoundRef[] SOUNDS = {
            GunFire
    };

    public final static class SoundRef {
        final String id;

        private SoundRef(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SoundRef soundRef = (SoundRef) o;
            return id.equals(soundRef.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    private Sounds() {
        throw new UnsupportedOperationException("Must not be instantiated");
    }
}
