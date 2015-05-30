package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.engine.bloom.Bloom;
import mountainrangepvp.engine.ui.TextRenderer;
import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.game.world.Session;
import mountainrangepvp.game.world.Snapshot;

import java.time.Duration;

/**
 * @author lachlan
 */
public class WorldRenderer {

    private static final Color SKY_COLOUR = new Color(0.07f, 0.09f, 0.19f, 1);

    private final Vector2 screen;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    private final EventBus eventBus;
    private final Session session;

    private final TextRenderer textRenderer;

    private final BackgroundRenderer backgroundRenderer;
    private final TerrainRenderer terrainRenderer;
    private final EntityRenderer entityRenderer;
    private final ChatRenderer chatRenderer;
    private final MiniMapRenderer miniMapRenderer;
    private final LeaderBoardRenderer leaderBoardRenderer;
    private final Bloom bloom;

    private final Texture crossHairTexture;

    public WorldRenderer(EventBus eventBus, Session session) {
        this.eventBus = eventBus;
        this.session = session;

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
        entityRenderer = new EntityRenderer(batch, textRenderer);
        chatRenderer = new ChatRenderer(batch, textRenderer);
        miniMapRenderer = new MiniMapRenderer(batch);
        leaderBoardRenderer = new LeaderBoardRenderer(batch, textRenderer);

        bloom = new Bloom(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                          false, false, true);
        bloom.setTreshold(0.6f);
        bloom.blurPasses = 2;
    }

    public void render(Vector2 scroll, Duration pingTime) {
        Snapshot snapshot = session.getSnapshot();

        bloom.startCapture();
        Gdx.gl.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backgroundRenderer.render(scroll);
        entityRenderer.render(scroll, snapshot, session.localPlayerEntity().isDefined());
        terrainRenderer.render(scroll, session.getTerrain());
        drawCrosshair();

        bloom.stopCaptureAndRender();


        miniMapRenderer.render(scroll, snapshot, session.getTerrain());
        leaderBoardRenderer.render(snapshot);
        chatRenderer.render(snapshot, session.chatManager());

        drawDebug(pingTime, snapshot);
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

    private void drawDebug(Duration pingTime, Snapshot snapshot) {
        String pingMillis = String.valueOf(pingTime.toMillis());
        int entityCount = snapshot.entities().size();

        textRenderer.setSize(15);
        textRenderer.setColour(Color.RED);
        textRenderer.drawString(batch, Gdx.graphics.getFramesPerSecond() + " fps", 10, screen.y - 10);
        textRenderer.drawString(batch, eventBus.getMessagesPerFrame() + " mpf", 10, screen.y - 30);
        textRenderer.drawString(batch, pingMillis + " ms ping", 10, screen.y - 50);
        textRenderer.drawString(batch, entityCount + " entities", 10, screen.y - 70);
        textRenderer.setColour(Color.BLACK);
    }
}
