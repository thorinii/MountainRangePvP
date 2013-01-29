/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.renderer.HeightMapRenderer;

/**
 *
 * @author lachlan
 */
public class GameScreen implements Screen {

    private final HeightMapRenderer heightMapRenderer;

    public GameScreen(int seed) {
        heightMapRenderer = new HeightMapRenderer(new MountainHeightMap(seed));
    }

    @Override
    public void render(float delta) {
        // TODO: update

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        heightMapRenderer.render(0);
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
