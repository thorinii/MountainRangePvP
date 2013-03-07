/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.chat.ChatLine;
import mountainrangepvp.chat.ChatManager;
import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class ChatRenderer implements Renderer {

    private static final Color[] COLOURS = new Color[]{
        new Color(1, .5f, 0, 1),
        new Color(1, 0, 0, 1),
        new Color(0, 1, 0, 1),
        new Color(0, 0, 1, 1)};
    private final SpriteBatch batch;
    private final TextRenderer textRenderer;
    private final ChatManager chatManager;
    //
    private final Texture background;

    public ChatRenderer(SpriteBatch batch, TextRenderer textRenderer,
            ChatManager chatManager) {
        this.batch = batch;
        this.chatManager = chatManager;
        this.textRenderer = textRenderer;

        background = new Texture(Gdx.files.internal("chat/background.png"));
    }

    @Override
    public void render(Vector2 scroll) {
        batch.begin();
        drawChatMessages();

        if (chatManager.isChatting())
            drawCurrentChat();
        batch.end();
    }

    private void drawChatMessages() {
        int i = 0;

        for (ChatLine line : chatManager.getLinesHead(20)) {
            if (!chatManager.isChatting() && line.isOld())
                continue;
            if (line.getText().startsWith("/"))
                continue;

            Player p = line.getPlayer();
            String name = line.getPlayerName();

            String text = name + ": " + line.getText();

            if (p == null)
                textRenderer.setColour(Color.BLACK);
            else
                textRenderer.setColour(COLOURS[p.getTeam().ordinal()]);
            textRenderer.drawString(batch, text, 15, 45 + 15 + i * 25);

            i++;
        }
    }

    private void drawCurrentChat() {
        batch.draw(background, 10, 10, 400, 31);
        textRenderer.drawString(batch, chatManager.getCurrentLine(),
                                15, 10 + 15 + 8);
    }
}
