package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 *
 * @author lachlan
 */
public class TextRenderer {

    private final FreeTypeFontGenerator generator;
    private final BitmapFont[] fonts;
    private BitmapFont current;
    private Color c;

    public TextRenderer() {
        generator = new FreeTypeFontGenerator(Gdx.files.
                classpath("arial.ttf"));
        fonts = new BitmapFont[100];

        setSize(20);
    }

    public void setSize(int size) {
        if (fonts[size] != null) {
            current = fonts[size];
        } else {
            BitmapFont font = generator.generateFont(size);
            font.setColor(0, 0, 0, 1);

            fonts[size] = font;
            current = font;
        }
    }

    public void setColour(Color c) {
        current.setColor(c);
    }

    public void drawString(SpriteBatch batch, String string, int x, int y) {
        current.draw(batch, string, x, y);
    }

    public void drawStringCentred(SpriteBatch batch, String string, int x, int y) {
        BitmapFont.TextBounds bounds = current.getBounds(string);
        x -= bounds.width / 2;
        y -= bounds.height / 2;
        drawString(batch, string, x, y);
    }
}
