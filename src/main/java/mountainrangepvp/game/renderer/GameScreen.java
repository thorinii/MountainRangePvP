package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.LegacyLog;
import mountainrangepvp.game.world.Session;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import java.nio.IntBuffer;

/**
 * @author lachlan
 */
public class GameScreen implements Screen {

    private static final Color SKY_COLOUR = new Color(0.564f, 0.745f, 0.898f, 1);

    private final WorldRenderer renderer;
    private final int width, height;

    private final Session session;
    private final Vector2 cameraPosition;

    public GameScreen(EventBus eventbus, Session session) {
        this.session = session;
        renderer = new WorldRenderer(eventbus, session);

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        /*
         * Hide the cursor
         */
        if (Mouse.isCreated()) {
            try {
                int min = Cursor.getMinCursorSize();
                IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
                Cursor emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp,
                                                null);

                Mouse.setNativeCursor(emptyCursor);
            } catch (LWJGLException ex) {
                LegacyLog.warn("Error hiding mouse:", ex);
            }
        }

        cameraPosition = new Vector2(0, 0);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (!session.hasMap()) return;

        Vector2 pos = session.playerManager.getLocalPlayer().getCentralPosition();

        pos.x = pos.x - width / 2;
        pos.y = pos.y - height / 2;

        cameraPosition.lerp(pos, 0.2f);

        renderer.render(cameraPosition);
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
