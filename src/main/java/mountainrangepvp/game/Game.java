package mountainrangepvp.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.GameClient;
import mountainrangepvp.mp.message.MessageListener;
import mountainrangepvp.renderer.GameScreen;
import mountainrangepvp.util.Log;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.physics.PhysicsSystem;
import mountainrangepvp.world.player.ClientPlayerManager;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ClientShotManager;
import mountainrangepvp.world.shot.ShotManager;

import java.io.IOException;

/**
 * Created by lachlan on 15/04/15.
 */
public abstract class Game implements ApplicationListener, MessageListener {
    private final String serverIP;

    protected final GameConfig config;

    private final GameClient client;

    protected final GameWorld world;
    protected PhysicsSystem physicsSystem;
    protected InputHandler inputHandler;
    protected AudioManager audioManager;

    protected GameScreen gameScreen;

    public Game(GameConfig config) {
        this.serverIP = config.serverIP;
        this.config = config;

        world = new GameWorld();
        world.setTeamModeOn(config.teamModeOn);

        PlayerManager playerManager = new ClientPlayerManager(config.playerName, config.team);
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ClientShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        client = new GameClient(world, serverIP);
        client.addMessageListener(this);
    }

    @Override
    public void create() {
        try {
            client.start();
        } catch (IOException ioe) {
            Log.crash("Error connecting to server", ioe);
        }
    }

    @Override
    public void dispose() {
        client.stop();
    }


    private float timeSinceLastUpdate = 0;

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        timeSinceLastUpdate += dt;

        if (timeSinceLastUpdate > config.TIMESTEP) {
            client.update();
            if (gameScreen != null) {
                inputHandler.update(config.TIMESTEP);
                world.update(config.TIMESTEP);
                physicsSystem.update(config.TIMESTEP);
            }

            timeSinceLastUpdate = 0;


            if (gameScreen != null) {
                gameScreen.render(dt);
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
