package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mountainrangepvp.engine.ui.TextRenderer;
import mountainrangepvp.game.world.ChatLine;
import mountainrangepvp.game.world.ChatManager;
import mountainrangepvp.game.world.ClientId;
import mountainrangepvp.game.world.Snapshot;
import scala.Option;
import scala.collection.JavaConversions;

/**
 * @author lachlan
 */
public class ChatRenderer {

    private static final Color[] COLOURS = new Color[]{
            new Color(1, .5f, 0, 1),
            new Color(1, 0, 0, 1),
            new Color(0, 1, 0, 1),
            new Color(0, 0, 1, 1)};
    private final SpriteBatch batch;
    private final TextRenderer textRenderer;

    private final Texture background;

    public ChatRenderer(SpriteBatch batch, TextRenderer textRenderer) {
        this.batch = batch;
        this.textRenderer = textRenderer;

        background = new Texture(Gdx.files.internal("chat/background.png"));
    }

    public void render(Snapshot snapshot, ChatManager chatManager) {
        batch.begin();
        drawChatMessages(snapshot, chatManager);

        if (chatManager.isChatting())
            drawCurrentChat(chatManager);
        batch.end();
    }

    private void drawChatMessages(Snapshot snapshot, ChatManager chatManager) {
        int i = 0;

        for (ChatLine line : JavaConversions.asJavaIterable(chatManager.getLinesHead(20))) {
            if (!chatManager.isChatting() && line.isOld())
                continue;
            if (line.text().startsWith("/"))
                continue;

            Option<ClientId> player = line.player();

            String text;
            if (player.isEmpty())
                text = line.text();
            else
                text = snapshot.nicknameFor(player.get()) + ": " + line.text();

            if (player.isEmpty())
                textRenderer.setColour(Color.BLACK);
            else
                textRenderer.setColour(COLOURS[0]);
            textRenderer.drawString(batch, text, 15, 45 + 15 + i * 25);

            i++;
        }

        textRenderer.setColour(Color.BLACK);
    }

    private void drawCurrentChat(ChatManager chatManager) {
        batch.draw(background, 10, 10, 400, 31);
        textRenderer.drawString(batch, chatManager.getCurrentLine(),
                                15, 10 + 15 + 8);
    }
}
