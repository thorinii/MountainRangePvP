/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.terrain.HeightMap;
import mountainrangepvp.terrain.Terrain;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class WorldRenderer {

    private static final Color SKY_COLOUR = new Color(0.564f, 0.745f, 0.898f, 1);
    //
    private final Vector2 screen;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    //
    private Terrain terrain;
    private PlayerManager playerManager;
    //
    private BackgroundRenderer backgroundRenderer;
    private HeightMapRenderer heightMapRenderer;
    private PlayerRenderer playerRenderer;
    private ShotRenderer shotRenderer;
    private MiniMapRenderer miniMapRenderer;
    private LeaderboardRenderer leaderboardRenderer;
    //
    private TextRenderer textRenderer;
    //    
    private final Texture crossHairTexture;

    public WorldRenderer() {
        screen = new Vector2(Gdx.graphics.getWidth() + 1,
                             Gdx.graphics.getHeight());

        batch = new SpriteBatch();

        camera = new OrthographicCamera(screen.x, screen.y);
        camera.position.set(screen.x / 2, screen.y / 2, 0);
        camera.update();

        crossHairTexture = new Texture(Gdx.files.internal("crosshair.png"));

        backgroundRenderer = new BackgroundRenderer(batch);
        textRenderer = new TextRenderer();
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        playerRenderer = new PlayerRenderer(batch, textRenderer, playerManager);

        leaderboardRenderer = new LeaderboardRenderer(batch, playerManager,
                                                      textRenderer);

        if (terrain != null)
            miniMapRenderer = new MiniMapRenderer(batch, terrain,
                                                  playerManager);
    }

    public void setShotManager(ShotManager shotManager) {
        shotRenderer = new ShotRenderer(batch, shotManager);
    }

    public void setHeightMap(HeightMap heightMap) {
        this.terrain = new Terrain(heightMap);
        heightMapRenderer = new HeightMapRenderer(batch, terrain);

        if (playerManager != null)
            miniMapRenderer = new MiniMapRenderer(batch, terrain,
                                                  playerManager);
    }

    public void render(Vector2 scroll) {
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        backgroundRenderer.render(scroll);

        if (shotRenderer != null)
            shotRenderer.render(scroll);

        if (heightMapRenderer != null)
            heightMapRenderer.render(scroll);

        if (playerRenderer != null)
            playerRenderer.render(scroll);

        if (leaderboardRenderer != null)
            leaderboardRenderer.render(scroll);

        if (miniMapRenderer != null)
            miniMapRenderer.render(scroll);

        drawCrosshair();

    }

    private void drawCrosshair() {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();

        Vector2 mouse = new Vector2(x, y);

        mouse.x -= crossHairTexture.getWidth() / 2;
        mouse.y -= crossHairTexture.getHeight() / 2;

        batch.begin();
        batch.draw(crossHairTexture, mouse.x, mouse.y);
        batch.end();
    }
}
