package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * @author lachlan
 */
public class BackgroundRenderer {

    private static final float SCROLL_RATE_H = 0.003f;
    private static final float SCROLL_RATE_V = 0.03f;

    private final SpriteBatch batch;
    private final int width;
    private final int height;

    public BackgroundRenderer(SpriteBatch batch) {
        this.batch = batch;

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
    }

    public void render(Vector2 scroll) {
    }
}
