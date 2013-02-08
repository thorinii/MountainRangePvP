/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import mountainrangepvp.Log;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.renderer.HeightMapRenderer;
import mountainrangepvp.renderer.PlayerRenderer;
import mountainrangepvp.renderer.ShotRenderer;
import mountainrangepvp.shot.ShotManager;
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
    private final PlayerManager playerManager;
    private final HeightMap heightMap;
    private final HeightMapRenderer heightMapRenderer;
    private final PlayerRenderer playerRenderer;
    private final ShotRenderer shotRenderer;
    private final int width, height;

    public GameScreen(HeightMap heightMap,
            PlayerManager playerManager,
            ShotManager shotManager) {
        this.playerManager = playerManager;
        this.heightMap = heightMap;

        heightMapRenderer = new HeightMapRenderer(heightMap);
        playerRenderer = new PlayerRenderer(playerManager);
        shotRenderer = new ShotRenderer(shotManager);

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
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        Vector2 pos = playerManager.getLocalPlayer().getPosition().cpy();

        pos.x = pos.x - width / 2 + Player.WIDTH / 2;
        pos.y = pos.y - height / 2 + Player.HEIGHT / 2;

        shotRenderer.render((int) pos.x, (int) pos.y);
        heightMapRenderer.render((int) pos.x, (int) pos.y);
        playerRenderer.render((int) pos.x, (int) pos.y);
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
