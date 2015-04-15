package mountainrangepvp.renderer;

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
    private final Texture sunTexture;
    private final int width;
    private final int height;

    public BackgroundRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.sunTexture = new Texture(Gdx.files.internal(
                "background/sun.png"));

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
    }

    public void render(Vector2 scroll) {
        float sunX = -scroll.x * SCROLL_RATE_H + width * 3 / 4;
        float sunY = -scroll.y * SCROLL_RATE_V + height * 3 / 4;

        batch.begin();
        batch.draw(sunTexture, sunX, sunY);
        batch.end();
    }
}
