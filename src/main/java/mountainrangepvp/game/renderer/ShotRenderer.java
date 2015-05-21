package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.world.ShotEntity;
import mountainrangepvp.game.world.Snapshot;
import scala.collection.JavaConversions;

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

    public void render(final Vector2 scroll, Snapshot snapshot) {
        batch.begin();

        Vector2 position = new Vector2();
        for (ShotEntity shot : JavaConversions.asJavaIterable(snapshot.shots())) {
            position.set(shot.position())
                    .sub(scroll);

            float angle = shot.velocity().angle();

            batch.draw(shotTexture,
                       position.x, position.y,
                       0, 0, // Origin
                       shotTexture.getWidth(), shotTexture.getHeight(), // Dst WH
                       1, 1, // Scale
                       angle, // Rotation
                       0, 0, // Src XY
                       shotTexture.getWidth(), shotTexture.getHeight(), // Src WH
                       false, false); // Flip
        }

        batch.end();
    }
}
