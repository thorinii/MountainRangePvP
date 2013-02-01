/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;

/**
 *
 * @author lachlan
 */
public class InputHandler implements InputProcessor {

    private final PlayerManager playerManager;
    //
    private boolean up, left, right;

    public InputHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void register() {
        Gdx.input.setInputProcessor(this);
    }

    public void update(float dt) {
        Player local = playerManager.getLocalPlayer();
        Vector2 vel = local.getVelocity();

        if (local.isOnGround()) {
            if (left) {
                vel.x = accelerate(vel.x, -5, -Player.WALK_SPEED);
            } else if (right) {
                vel.x = accelerate(vel.x, 5, Player.WALK_SPEED);
            } else {
                vel.x *= Player.FRICTION;
            }
        } else {
            if (left) {
                vel.x = accelerate(vel.x, -1, -Player.AIR_SPEED);
            } else if (right) {
                vel.x = accelerate(vel.x, 5, Player.AIR_SPEED);
            }
        }
    }

    private float accelerate(float v, float accel, float max) {
        if (Math.abs(v) < Math.abs(max)) {
            v += accel;
            return v;
        } else if (Math.signum(v) != Math.signum(max)) {
            v += accel;
            return v;
        } else {
            v -= accel * 0.04f;
            return v;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                up = true;
                break;
            case Keys.A:
                left = true;
                break;
            case Keys.D:
                right = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.W:
                up = false;
                break;
            case Keys.A:
                left = false;
                break;
            case Keys.D:
                right = false;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
    }
}
