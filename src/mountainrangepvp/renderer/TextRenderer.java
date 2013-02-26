/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 *
 * @author lachlan
 */
public class TextRenderer {

    private final BitmapFont font;
    private final SpriteBatch privateBatch;

    public TextRenderer() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.
                classpath("arial.ttf"));
        font = generator.generateFont(20);
        font.setColor(0, 0, 0, 1);

        privateBatch = new SpriteBatch();
    }

    public void drawString(String string, SpriteBatch batch, int x, int y) {
        font.draw(batch, string, x, y);
    }

    /**
     * Draws a string. Not as efficient as drawString(String string, SpriteBatch
     * batch, int x, int y)
     * <p/>
     * @param string
     * @param x
     * @param y
     */
    public void drawString(String string, int x, int y) {
        privateBatch.begin();
        font.draw(privateBatch, string, x, y);
        privateBatch.end();
    }
}
