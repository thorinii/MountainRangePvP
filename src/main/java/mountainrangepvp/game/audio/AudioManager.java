package mountainrangepvp.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.game.world.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lachlan
 */
public class AudioManager {

    private final PlayerManager playerManager;
    private final GameSettings config;
    private Map<Sounds.SoundRef, Sound> sounds;

    public AudioManager(PlayerManager playerManager, GameSettings config) {
        this.playerManager = playerManager;
        this.config = config;

        this.sounds = new HashMap<>();
    }

    public void loadAudio() {
        Map<Sounds.SoundRef, Sound> tmp = new HashMap<>();
        for (Sounds.SoundRef ref : Sounds.SOUNDS) {
            tmp.put(ref, Gdx.audio.newSound(Gdx.files.internal(ref.id)));
        }

        sounds = tmp;
    }

    public void listenTo(ShotManager shotManager) {
        shotManager.addShotListener(new AudioShotListener());
    }

    public void play(Sounds.SoundRef sound) {
        if (!config.audioOn) return;

        Sound s = sounds.get(sound);
        s.play();
    }

    public void play(Sounds.SoundRef sound, Vector2 source) {
        if (!config.audioOn) return;

        Sound s = sounds.get(sound);

        Player lp = playerManager.getLocalPlayer();

        Vector2 diff = source.cpy().sub(lp.getPosition());
        float volume = MathUtils.clamp(1000f / diff.len(), 0, 1);
        float pitch = MathUtils.clamp(Math.abs(lp.getVelocity().x) / 10f * Math.signum(diff.x),
                                      -0.1f, 0.1f);
        float pan = MathUtils.clamp(-diff.x / 100f, -1, 1);

        s.play(volume, 1 + pitch, pan);
    }

    private class AudioShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            play(Sounds.GunFire, shot.base);
        }

        @Override
        public void shotTerrainCollision(Shot shot) {
        }

        @Override
        public void shotPlayerCollision(Shot shot, Player player) {
        }
    }
}
