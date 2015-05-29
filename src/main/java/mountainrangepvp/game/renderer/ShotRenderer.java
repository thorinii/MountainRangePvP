package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.world.ShotEntity;
import org.lwjgl.opengl.GL11;

/**
 * @author lachlan
 */
public class ShotRenderer {

    private final SpriteBatch batch;
    private final Texture shotTexture;

    public ShotRenderer(SpriteBatch batch) {
        this.batch = batch;

        shotTexture = new Texture(Gdx.files.internal("shot/shot-ball.png"));
    }

    public void renderShot(Vector2 scroll, ShotEntity shot) {
        int prevSrc = batch.getBlendSrcFunc();
        int prevDst = batch.getBlendDstFunc();

        batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Vector2 position = shot.position().cpy()
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


        batch.setBlendFunction(prevSrc, prevDst);
    }
}
