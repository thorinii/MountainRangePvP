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
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class PlayerRenderer implements Renderer {

    private final PlayerManager playerManager;
    private final int width, height;
    private final SpriteBatch batch;
    private final Texture crossHairTexture;
    private final Texture bodyOrangeTexture;
    private final Texture armsTexture;

    public PlayerRenderer(SpriteBatch batch, PlayerManager playerManager) {
        this.batch = batch;
        this.playerManager = playerManager;

        width = Gdx.graphics.getWidth() + 1;
        height = Gdx.graphics.getHeight();

        crossHairTexture = new Texture(Gdx.files.internal("crosshair.png"));
        bodyOrangeTexture = new Texture(Gdx.files.internal(
                "player/body-orange.png"));
        armsTexture = new Texture(Gdx.files.internal("player/arms.png"));
    }

    public void render(Vector2 scroll) {
        batch.begin();

        for (Player player : playerManager.getPlayers()) {
            if (player.isAlive()) {
                drawPlayer(player, scroll);
            }
        }

        drawGun(playerManager.getLocalPlayer(), scroll);

        batch.end();
    }

    private void drawPlayer(Player player, Vector2 scroll) {
        Vector2 pos = player.getPosition().cpy();
        pos.sub(scroll);

        if (pos.x < -Player.WIDTH || pos.x > width) {
            return;
        }
        if (pos.y < -Player.HEIGHT || pos.y > height) {
            return;
        }

        Vector2 ppos = player.getPosition().cpy();
        ppos.x += Player.WIDTH / 2;
        ppos.y += 60;
        Vector2 dir = player.getGunPosition().cpy().sub(ppos).nor();

//        if (player == playerManager.getLocalPlayer()) {
//                shapeRenderer.setColor(LOCAL_PLAYER_COLOUR);
//        } else {
//                shapeRenderer.setColor(REMOTE_PLAYER_COLOUR);
//        }

        batch.draw(bodyOrangeTexture,
                   pos.x, pos.y, // Position
                   0, 0, // Origin
                   bodyOrangeTexture.getWidth(), bodyOrangeTexture.getHeight(), // Dst WH
                   1, 1, // Scale
                   0, // Rotation
                   0, 0, // Src XY
                   bodyOrangeTexture.getWidth(), bodyOrangeTexture.getHeight(), // Src WH
                   dir.x > 0, false);

        batch.draw(armsTexture,
                   pos.x - 24, pos.y + 40, // Position
                   43, 17, // Origin
                   armsTexture.getWidth(), armsTexture.getHeight(), // Dst WH
                   1, 1, // Scale
                   dir.angle() + 180 % 360, // Rotation
                   0, 0, // Src XY
                   armsTexture.getWidth(), armsTexture.getHeight(), // Src WH
                   false, false); // Flip
    }

    private void drawGun(Player localPlayer, Vector2 scroll) {
        Vector2 gun = localPlayer.getGunPosition().cpy();
        gun.sub(scroll);

        gun.x -= crossHairTexture.getWidth() / 2;
        gun.y -= crossHairTexture.getHeight() / 2;

        batch.draw(crossHairTexture, gun.x, gun.y);
    }
}
