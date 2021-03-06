package mountainrangepvp.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.core.Session;
import mountainrangepvp.engine.Log;
import mountainrangepvp.engine.util.EventBus;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import java.nio.IntBuffer;
import java.time.Duration;

/**
 * @author lachlan
 */
public class GameScreen {

    private static final Color SKY_COLOUR = new Color(0.564f, 0.745f, 0.898f, 1);

    private final Log log;
    private final WorldRenderer renderer;
    private final int width, height;

    private final Session session;

    public GameScreen(Log log, EventBus eventBus, Session session) {
        this.log = log;
        this.session = session;
        renderer = new WorldRenderer(eventBus, session);

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        hideCursor();
    }

    private void hideCursor() {
        if (Mouse.isCreated()) {
            try {
                int min = Cursor.getMinCursorSize();
                IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
                Cursor emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp,
                                                null);

                Mouse.setNativeCursor(emptyCursor);
            } catch (LWJGLException ex) {
                log.warn("Error hiding mouse:", ex);
            }
        }
    }


    public void render(float delta, Duration pingTime) {
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        Vector2 scroll = session.getCameraCentre().cpy();
        scroll.x = scroll.x - width / 2;
        scroll.y = scroll.y - height / 2;

        renderer.render(scroll, pingTime);
    }
}
