package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.world.Instance;

/**
 * @author lachlan
 */
public class WorldRenderer {

    private static final Color SKY_COLOUR = new Color(0.564f, 0.745f, 0.898f, 1);

    private final Vector2 screen;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    private final Instance instance;

    private final TextRenderer textRenderer;

    private final BackgroundRenderer backgroundRenderer;
    private final TerrainRenderer terrainRenderer;
    private final PlayerRenderer playerRenderer;
    private final ShotRenderer shotRenderer;
    private final ChatRenderer chatRenderer;
    private final MiniMapRenderer miniMapRenderer;
    private final LeaderboardRenderer leaderboardRenderer;

    private final Texture crossHairTexture;

    public WorldRenderer(Instance instance) {
        this.instance = instance;

        screen = new Vector2(Gdx.graphics.getWidth() + 1,
                             Gdx.graphics.getHeight());

        batch = new SpriteBatch();

        camera = new OrthographicCamera(screen.x, screen.y);
        camera.position.set(screen.x / 2, screen.y / 2, 0);
        camera.update();

        crossHairTexture = new Texture(Gdx.files.internal("crosshair.png"));

        textRenderer = new TextRenderer();

        backgroundRenderer = new BackgroundRenderer(batch);
        terrainRenderer = new TerrainRenderer(batch);
        playerRenderer = new PlayerRenderer(batch, textRenderer);
        shotRenderer = new ShotRenderer(batch);
        chatRenderer = new ChatRenderer(batch, textRenderer);
        miniMapRenderer = new MiniMapRenderer(batch);
        leaderboardRenderer = new LeaderboardRenderer(batch, textRenderer);
    }

    public void render(Vector2 scroll) {
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backgroundRenderer.render(scroll);
        shotRenderer.render(scroll, instance.getMap().shotManager);
        terrainRenderer.render(scroll, instance.getMap().terrain);
        playerRenderer.render(scroll, instance.playerManager);
        chatRenderer.render(instance.chatManager);
        leaderboardRenderer.render(scroll, instance.playerManager);
        miniMapRenderer.render(scroll, instance.playerManager, instance.getMap().terrain);

        drawCrosshair();

        textRenderer.setSize(15);
        textRenderer.setColour(Color.RED);
        textRenderer.drawString(batch, Gdx.graphics.getFramesPerSecond() + " fps", 10, screen.y - 10);
        textRenderer.setColour(Color.BLACK);
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
