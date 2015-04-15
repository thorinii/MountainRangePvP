package mountainrangepvp.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.world.Instance;
import mountainrangepvp.game.world.ChatManager;
import mountainrangepvp.game.world.Player;
import mountainrangepvp.game.world.ShotManager;

/**
 * @author lachlan
 */
public class InputHandler {

    private static final int DOUBLE_JUMP_MIN = 50;
    private static final int DOUBLE_JUMP_MAX = 500;
    private static final int GUN_RATE = 100;

    private final ChatManager chatManager;
    private final PlayerInputHandler playerInputHandler;
    private final ChatInputHandler chatInputHandler;

    private ShotManager shotManager;

    private boolean up, down, left, right;
    private int doubleJumpTimer;

    private boolean gun;
    private int gunTimer;

    public InputHandler(ChatManager chatManager) {
        this.chatManager = chatManager;

        playerInputHandler = new PlayerInputHandler();
        chatInputHandler = new ChatInputHandler();
    }

    public void setShotManager(ShotManager shotManager) {
        this.shotManager = shotManager;
    }

    public void register() {
        Gdx.input.setInputProcessor(playerInputHandler);
    }

    public void update(Instance world, float dt) {
        Player local = world.playerManager.getLocalPlayer();
        if (!local.isAlive()) {
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

            gun = false;
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

        if (down) {
            vel.y -= Player.DOWN_ACCELERATION;
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

        Vector2 target = new Vector2(x, y);
        target.x -= Gdx.graphics.getWidth() / 2;
        target.y -= Gdx.graphics.getHeight() / 2;
        target.nor();

        Vector2 dir = player.getGunDirection();
        float lerpSpeed = Math.min(0.8f, Math.max(0.3f, 10f / player.getVelocity().x));
        dir.lerp(target, lerpSpeed);
    }

    private void doShooting(Player player) {
        if (gunTimer > GUN_RATE) {
            gunTimer = 0;

            Vector2 pos = player.getCentralPosition();
            // TODO: send a message
            shotManager.addShot(pos,
                                player.getGunDirection().cpy(),
                                player);

            Vector2 kickback = player.getGunDirection().cpy().scl(-90f);
            player.getVelocity().add(kickback);
        }
    }

    private void reset() {
        up = down = left = right = false;
        gun = false;

        doubleJumpTimer = 0;
        gunTimer = 0;
    }

    class PlayerInputHandler extends LibGdxInputAdaptor {

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
                case Keys.S:
                    down = true;
                    break;
                case Keys.ESCAPE:
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
                case Keys.S:
                    down = false;
                    break;
                case Keys.TAB:
                    Gdx.input.setInputProcessor(chatInputHandler);
                    chatManager.setChatting(true);
                    reset();
                    break;
            }
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer,
                                 int button) {
            gun = true;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            gun = false;
            return true;
        }
    }

    class ChatInputHandler extends LibGdxInputAdaptor {

        @Override
        public boolean keyUp(int keycode) {
            String line = chatManager.getCurrentLine();
            switch (keycode) {
                case Keys.ENTER:
                    if (!line.isEmpty())
                        chatManager.addLocalLine(line);
                case Keys.ESCAPE:
                case Keys.TAB:
                    Gdx.input.setInputProcessor(playerInputHandler);
                    chatManager.setChatting(false);
                    break;
                case Keys.BACKSPACE:
                    if (!line.isEmpty())
                        line = line.substring(0, line.length() - 1);
                    break;
            }

            chatManager.setCurrentLine(line);
            return true;
        }

        @Override
        public boolean keyTyped(char character) {
            boolean acceptable = ("" + character).matches(
                    "[a-zA-Z0-9`\\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\\\\\|/\\.,<>\\?]");

            if (!acceptable)
                return true;

            String line = chatManager.getCurrentLine();
            line += character;
            chatManager.setCurrentLine(line);

            return true;
        }
    }
}
