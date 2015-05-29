package mountainrangepvp.engine;

/**
 * Created by lachlan on 16/04/15.
 */
public final class SoundRef {
    final String id;

    public SoundRef(String id) {
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
