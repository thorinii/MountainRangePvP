/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.renderer.HeightMapRenderer;
import mountainrangepvp.renderer.PlayerRenderer;

/**
 *
 * @author lachlan
 */
public class GameScreen implements Screen {

    private final PlayerManager playerManager;
    private final HeightMap heightMap;
    private final HeightMapRenderer heightMapRenderer;
    private final PlayerRenderer playerRenderer;
    private final int width, height;

    public GameScreen(int seed, PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.heightMap = new MountainHeightMap(seed);

        heightMapRenderer = new HeightMapRenderer(heightMap);
        playerRenderer = new PlayerRenderer(playerManager);

        Player p = playerManager.getLocalPlayer();
        p.getPosition().x = 100;
        p.getPosition().y = heightMap.getBlock(100, 1)[0];

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        Vector2 pos = playerManager.getLocalPlayer().getPosition();

        heightMapRenderer.render((int) pos.x - width / 2,
                                 (int) pos.y - height / 2);
        playerRenderer.render((int) pos.x - width / 2, (int) pos.y - height / 2);
        
        pos.x++;
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
