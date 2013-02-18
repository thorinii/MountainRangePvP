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
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class InputHandler implements InputProcessor {

    private final PlayerManager playerManager;
    private final ShotManager shotManager;
    private final int DOUBLE_JUMP_MIN = 50;
    private final int DOUBLE_JUMP_MAX = 500;
    private final int GUN_RATE = 100;
    //
    private boolean up, left, right;
    private int doubleJumpTimer;
    //
    private boolean gun;
    private int gunTimer;

    public InputHandler(PlayerManager playerManager, ShotManager shotManager) {
        this.playerManager = playerManager;
        this.shotManager = shotManager;
    }

    public void register() {
        Gdx.input.setInputProcessor(this);
    }

    public void update(float dt) {
        Player local = playerManager.getLocalPlayer();
        if (!local.isAlive()) {
            //&& !playerManager.getLocalPlayer().getName().equals("Lachlan")) {
            gun = false;
            doubleJumpTimer = 0;
            gunTimer = 0;
            return;
        }

        Vector2 vel = local.getVelocity();

        doPlayerWalking(local, vel, dt);
        doGunControl(local);

        if (gun) {
            doShooting(local);

            //if (!playerManager.getLocalPlayer().getName().equals("Lachlan")) {
            gun = false;
            //}
        }

        gunTimer += (int) (1000 * dt);
    }

    private void doPlayerWalking(Player local, Vector2 vel, float dt) {
        if (local.isOnGround()) {
            if (left) {
                vel.x = accelerate(vel.x, -Player.WALK_ACCELERATION,
                                   -Player.WALK_SPEED);
            } else if (right) {
                vel.x = accelerate(vel.x, Player.WALK_ACCELERATION,
                                   Player.WALK_SPEED);
            } else {
                vel.x *= Player.FRICTION;
            }

            if (up) {
                vel.y = Player.JUMP_SPEED;
                doubleJumpTimer = 0;
            }
        } else {
            if (left) {
                vel.x = accelerate(vel.x, -Player.AIR_ACCELERATION,
                                   -Player.AIR_SPEED);
            } else if (right) {
                vel.x = accelerate(vel.x, Player.AIR_ACCELERATION,
                                   Player.AIR_SPEED);
            }

            if (!up) {
                doubleJumpTimer += (int) (dt * 1000);
            } else {
                if (doubleJumpTimer > DOUBLE_JUMP_MIN && doubleJumpTimer < DOUBLE_JUMP_MAX) {
                    vel.y = Player.JUMP_SPEED;
                    doubleJumpTimer = DOUBLE_JUMP_MAX;
                }
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

    private void doGunControl(Player player) {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();

        Vector2 dir = new Vector2(x, y);
        dir.x -= Gdx.graphics.getWidth() / 2;
        dir.y -= Gdx.graphics.getHeight() / 2;
        dir.nor();

        player.getGunDirection().set(dir);
    }

    private void doShooting(Player player) {
        if (gunTimer > GUN_RATE) {
            gunTimer = 0;

            Vector2 pos = player.getCentralPosition();
            shotManager.addShot(pos,
                                player.getGunDirection().cpy(),
                                player);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
            case Keys.W:
                up = true;
                break;
            case Keys.A:
                left = true;
                break;
            case Keys.D:
                right = true;
                break;
            case Keys.ESCAPE:
                System.out.println("Exit");
                Gdx.app.exit();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
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
        gun = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        gun = false;
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
