package mountainrangepvp.game;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.game.world.*;

/**
 * Plays gunshot sounds when guns are fired.
 */
public class AudioShotListener implements ShotListener {

    private final PlayerManager playerManager;
    private final AudioManager audioManager;

    public AudioShotListener(PlayerManager playerManager, AudioManager audioManager) {
        this.playerManager = playerManager;
        this.audioManager = audioManager;
    }

    @Override
    public void shotAdd(Shot shot) {
        Old_Player lp = playerManager.getLocalPlayer();

        Vector2 diff = shot.base.cpy().sub(lp.getPosition());
        audioManager.play(Sounds.GunFire, diff);
    }

    @Override
    public void shotTerrainCollision(Shot shot) {
    }

    @Override
    public void shotPlayerCollision(Shot shot, Old_Player player) {
    }
}
