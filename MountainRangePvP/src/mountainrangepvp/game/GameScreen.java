/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.renderer.HeightMapRenderer;
import mountainrangepvp.renderer.PlayerRenderer;
import mountainrangepvp.renderer.ShotRenderer;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class GameScreen implements Screen {

    private static final Color SKY_COLOUR = new Color(0.564f, 0.745f, 0.898f, 1);
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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        Vector2 pos = playerManager.getLocalPlayer().getPosition().cpy();
        pos.x = pos.x - width / 2 + Player.WIDTH / 2;
        pos.y = pos.y - height / 2 + Player.HEIGHT / 2;


        heightMapRenderer.render((int) pos.x, (int) pos.y);
        playerRenderer.render((int) pos.x, (int) pos.y);
        shotRenderer.render((int) pos.x, (int) pos.y);
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
