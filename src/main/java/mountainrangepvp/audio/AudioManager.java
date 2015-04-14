package mountainrangepvp.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.GameConfig;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotListener;
import mountainrangepvp.shot.ShotManager;

/**
 * @author lachlan
 */
public class AudioManager {

    private final Player localPlayer;
    private final GameConfig config;
    private Sound gunShot;


    public AudioManager(PlayerManager playerManager, ShotManager shotManager, GameConfig config) {
        localPlayer = playerManager.getLocalPlayer();
        this.config = config;

        shotManager.addShotListener(new AudioShotListener());
    }

    public void loadAudio() {
        gunShot = Gdx.audio.newSound(Gdx.files.internal("shot/fire.ogg"));
    }

    private class AudioShotListener implements ShotListener {

        public AudioShotListener() {
        }

        @Override
        public void shotAdd(Shot shot) {
            Vector2 diff = shot.base.cpy().sub(localPlayer.getPosition());
            float volume = Math.min(1, Math.max(0, 1000f / diff.len()));

            if (config.audioOn)
                gunShot.play(volume);
        }

        @Override
        public void shotTerrainCollision(Shot shot) {
        }

        @Override
        public void shotPlayerCollision(Shot shot, Player player) {
        }
    }
}
