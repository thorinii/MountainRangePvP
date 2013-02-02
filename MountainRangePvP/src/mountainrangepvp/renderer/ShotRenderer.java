/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.shot.ShotManager;
import mountainrangepvp.shot.ShotManager.Shot;

/**
 *
 * @author lachlan
 */
public class ShotRenderer implements Renderer {

    private static final int SHOT_RADIUS = 3;
    private static final Color SHOT_COLOUR = new Color(1, 1, 1, 1);
//
    private final ShotManager shotManager;
    private final ShapeRenderer shapeRenderer;

    public ShotRenderer(ShotManager shotManager) {
        this.shotManager = shotManager;
        shapeRenderer = new ShapeRenderer();
    }

    public void render(int scrollx, int scrolly) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
        shapeRenderer.setColor(SHOT_COLOUR);

        for (Shot shot : shotManager.getShots()) {
            Vector2 shotPos = shot.position();

            shotPos.x -= scrollx;
            shotPos.y -= scrolly;

            shapeRenderer.filledCircle(shotPos.x, shotPos.y, SHOT_RADIUS);
        }

        shapeRenderer.end();
    }
}
