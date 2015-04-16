package mountainrangepvp.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lachlan
 */
public class AudioManager {

    private Map<SoundRef, Sound> sounds;
    private boolean muted;

    public AudioManager() {
        this.sounds = new HashMap<>();
        this.muted = false;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void loadAudio(SoundRef[] refs) {
        Map<SoundRef, Sound> tmp = new HashMap<>();
        for (SoundRef ref : refs) {
            tmp.put(ref, Gdx.audio.newSound(Gdx.files.internal(ref.id)));
        }

        sounds = tmp;
    }

    public void play(SoundRef sound) {
        if (muted) return;

        Sound s = sounds.get(sound);
        s.play();
    }

    public void play(SoundRef sound, Vector2 relativeSource) {
        if (muted) return;

        Sound s = sounds.get(sound);

        float volume = MathUtils.clamp(1000f / relativeSource.len(), 0, 1);
        float pan = MathUtils.clamp(-relativeSource.x / 100f, -1, 1);

        s.play(volume, 1, pan);
    }

}
