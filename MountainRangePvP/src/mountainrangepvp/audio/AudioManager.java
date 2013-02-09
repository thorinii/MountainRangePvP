/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotListener;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class AudioManager {

    private final Player localPlayer;
    private Sound gunShot;

    public AudioManager(PlayerManager playerManager, ShotManager shotManager) {
        localPlayer = playerManager.getLocalPlayer();

        shotManager.addShotListener(new AudioShotListener());
    }

    public void loadAudio() {
        gunShot = Gdx.audio.newSound(Gdx.files.internal("shot/gun-shot.ogg"));
    }

    private class AudioShotListener implements ShotListener {

        public AudioShotListener() {
        }

        @Override
        public void shotAdd(Shot shot) {
            Vector2 diff = shot.base.cpy().sub(localPlayer.getPosition());
            float volume = Math.min(1, Math.max(0, 1000f / diff.len()));

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
