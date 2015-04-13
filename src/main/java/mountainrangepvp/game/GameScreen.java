package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import java.nio.IntBuffer;
import mountainrangepvp.Log;
import mountainrangepvp.renderer.WorldRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

/**
 *
 * @author lachlan
 */
public class GameScreen implements Screen {

    private static final Color SKY_COLOUR = new Color(0.564f, 0.745f, 0.898f, 1);
    //
    private Cursor emptyCursor = null;
    //
    private final GameWorld world;
    private final WorldRenderer renderer;
    private final int width, height;

    public GameScreen(GameWorld world) {
        this.world = world;

        renderer = new WorldRenderer();
        renderer.setWorld(world);

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        /*
         * Hide the cursor
         */
        if (Mouse.isCreated()) {
            try {
                int min = Cursor.getMinCursorSize();
                IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
                emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp,
                                         null);

                Mouse.setNativeCursor(emptyCursor);
            } catch (LWJGLException ex) {
                Log.warn("Error hiding mouse:", ex);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Vector2 pos = world.getPlayerManager().getLocalPlayer().
                getCentralPosition();

        pos.x = pos.x - width / 2;
        pos.y = pos.y - height / 2;

        renderer.render(pos);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
