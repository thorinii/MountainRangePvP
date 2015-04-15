package mountainrangepvp.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.GameConfig;
import mountainrangepvp.world.player.Player;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.Shot;
import mountainrangepvp.world.shot.ShotListener;
import mountainrangepvp.world.shot.ShotManager;

/**
 * @author lachlan
 */
public class AudioManager {

    private final PlayerManager playerManager;
    private final GameConfig config;
    private Sound gunShot;


    public AudioManager(PlayerManager playerManager, ShotManager shotManager, GameConfig config) {
        this.playerManager = playerManager;
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
            Vector2 diff = shot.base.cpy().sub(playerManager.getLocalPlayer().getPosition());
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
