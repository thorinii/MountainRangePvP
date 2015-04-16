package mountainrangepvp.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mountainrangepvp.engine.ui.TextRenderer;
import mountainrangepvp.game.world.ChatLine;
import mountainrangepvp.game.world.ChatManager;
import mountainrangepvp.game.world.Player;

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

    public void render(ChatManager chatManager) {
        batch.begin();
        drawChatMessages(chatManager);

        if (chatManager.isChatting())
            drawCurrentChat(chatManager);
        batch.end();
    }

    private void drawChatMessages(ChatManager chatManager) {
        int i = 0;

        for (ChatLine line : chatManager.getLinesHead(20)) {
            if (!chatManager.isChatting() && line.isOld())
                continue;
            if (line.getText().startsWith("/"))
                continue;

            Player p = line.getPlayer();
            String name = line.getPlayerName();

            String text;
            if (p == null)
                text = line.getText();
            else
                text = name + ": " + line.getText();

            if (p == null)
                textRenderer.setColour(Color.BLACK);
            else
                textRenderer.setColour(COLOURS[p.getTeam().ordinal()]);
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
