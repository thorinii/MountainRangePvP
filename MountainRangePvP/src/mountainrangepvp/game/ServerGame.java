/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.ServerPlayerManager;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class ServerGame extends Game {

    private final HeightMap heightMap;
    private final ServerPlayerManager playerManager;
    private final ShotManager shotManager;
    private final PhysicsSystem physicsSystem;
    private final InputHandler inputHandler;
    private GameScreen gameScreen;

    public ServerGame(String playerName, int seed) {
        heightMap = new MountainHeightMap(seed);

        playerManager = new ServerPlayerManager(playerName);
        shotManager = new ShotManager(heightMap);
        physicsSystem = new PhysicsSystem(heightMap, playerManager);
        inputHandler = new InputHandler(playerManager, shotManager);
    }

    @Override
    public void create() {
        inputHandler.register();

        gameScreen = new GameScreen(heightMap, playerManager, shotManager);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        inputHandler.update(dt);
        shotManager.update(dt);
        physicsSystem.update(dt);
        gameScreen.render(dt);
    }
}
