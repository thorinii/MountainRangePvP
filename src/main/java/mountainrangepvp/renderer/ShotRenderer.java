package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class ShotRenderer implements Renderer {
//

    private final ShotManager shotManager;
    private final SpriteBatch batch;
    private final Texture shotTexture;

    public ShotRenderer(SpriteBatch batch, ShotManager shotManager) {
        this.batch = batch;
        this.shotManager = shotManager;

        shotTexture = new Texture(Gdx.files.internal("shot/shot.png"));
    }

    @Override
    public void render(Vector2 scroll) {
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
