package mountainrangepvp.game;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.AudioManager;
import mountainrangepvp.game.world.Old_Player;
import mountainrangepvp.game.world.PlayerManager;
import mountainrangepvp.game.world.ShotEntity;
import mountainrangepvp.game.world.ShotListener;

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
    public void shotAdd(ShotEntity shot) {
        Old_Player lp = playerManager.getLocalPlayer();

        Vector2 diff = shot.position().cpy().sub(lp.getPosition());
        audioManager.play(Sounds.GunFire, diff);
    }

    @Override
    public void shotTerrainCollision(ShotEntity shot) {
    }

    @Override
    public void shotPlayerCollision(ShotEntity shot, Old_Player player) {
    }
}
