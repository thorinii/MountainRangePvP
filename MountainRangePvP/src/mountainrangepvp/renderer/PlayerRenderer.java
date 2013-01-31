/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class PlayerRenderer {
    //

    private static final Color LOCAL_PLAYER_COLOUR = new Color(0, 1, 1, 1);
    private static final Color REMOTE_PLAYER_COLOUR = new Color(1, 1, 0, 1);
    //
    private final PlayerManager playerManager;
    private final int width, height;
    private final ShapeRenderer shapeRenderer;

    public PlayerRenderer(PlayerManager playerManager) {
        this.playerManager = playerManager;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
    }

    public void render(int scrollx, int scrolly) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledRectangle);

        for (Player player : playerManager.getPlayers()) {
            drawPlayer(player, scrollx, scrolly);
        }

        shapeRenderer.end();
    }

    private void drawPlayer(Player player, int scrollx, int scrolly) {
        Vector2 pos = player.getPosition().cpy();
        pos.x -= scrollx;
        pos.y -= scrolly;

        if (pos.x < -Player.WIDTH || pos.x > width) {
            return;
        }
        if (pos.y < -Player.HEIGHT || pos.y > height) {
            return;
        }

        if (player == playerManager.getLocalPlayer()) {
            shapeRenderer.setColor(LOCAL_PLAYER_COLOUR);
        } else {
            shapeRenderer.setColor(REMOTE_PLAYER_COLOUR);
        }

        shapeRenderer.filledRect(pos.x, pos.y, Player.WIDTH, Player.HEIGHT);
    }
}
