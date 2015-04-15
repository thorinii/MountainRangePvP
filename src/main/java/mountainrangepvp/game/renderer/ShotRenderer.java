package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.world.Shot;
import mountainrangepvp.game.world.ShotManager;

/**
 * @author lachlan
 */
public class ShotRenderer {

    private final SpriteBatch batch;
    private final Texture shotTexture;

    public ShotRenderer(SpriteBatch batch) {
        this.batch = batch;

        shotTexture = new Texture(Gdx.files.internal("shot/shot.png"));
    }

    public void render(Vector2 scroll, ShotManager shotManager) {
        batch.begin();

        for (Shot shot : shotManager.getShots()) {
            Vector2 shotPos = shot.position();
            shotPos.sub(scroll);

            Vector2 dir = shot.direction;
            dir.angle();

            batch.draw(shotTexture,
                       shotPos.x, shotPos.y, // Position
                       0, 0, // Origin
                       shotTexture.getWidth(), shotTexture.getHeight(), // Dst WH
                       1, 1, // Scale
                       dir.angle(), // Rotation
                       0, 0, // Src XY
                       shotTexture.getWidth(), shotTexture.getHeight(), // Src WH
                       false, false); // Flip
        }

        batch.end();
    }
}
