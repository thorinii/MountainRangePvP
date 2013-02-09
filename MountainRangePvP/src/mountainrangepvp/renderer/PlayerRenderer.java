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
public class PlayerRenderer implements Renderer {

    private static final Color LOCAL_PLAYER_COLOUR = new Color(0, 1, 1, 1);
    private static final Color REMOTE_PLAYER_COLOUR = new Color(1, 1, 0, 1);
    private static final Color CROSSHAIR_COLOUR = new Color(1, 0, 0, 1);
    private static final int CROSSHAIR = 40;
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
            if (player.isAlive()) {
                drawPlayer(player, scrollx, scrolly);
            }
        }
        shapeRenderer.end();

        drawGun(playerManager.getLocalPlayer(), scrollx, scrolly);
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

        shapeRenderer.filledRect((int) pos.x, (int) pos.y,
                                 Player.WIDTH, Player.HEIGHT);
    }

    private void drawGun(Player localPlayer, int scrollx, int scrolly) {
        Vector2 gun = localPlayer.getGunPosition().cpy();
        gun.x -= scrollx;
        gun.y -= scrolly;

        shapeRenderer.setColor(CROSSHAIR_COLOUR);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(
                gun.x - CROSSHAIR, gun.y,
                gun.x + CROSSHAIR, gun.y);
        shapeRenderer.line(
                gun.x, gun.y - CROSSHAIR,
                gun.x, gun.y + CROSSHAIR);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Circle);
        shapeRenderer.circle(gun.x, gun.y, CROSSHAIR * 3 / 4);
        shapeRenderer.circle(gun.x, gun.y, CROSSHAIR * 1 / 4);
        shapeRenderer.end();
    }
}
