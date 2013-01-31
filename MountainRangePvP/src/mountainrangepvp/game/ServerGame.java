/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.ServerPlayerManager;

/**
 *
 * @author lachlan
 */
public class ServerGame extends Game {

    private final HeightMap heightMap;
    private final ServerPlayerManager playerManager;
    private final PhysicsSystem physicsSystem;
    private GameScreen gameScreen;

    public ServerGame(String playerName, int seed) {
        heightMap = new MountainHeightMap(seed);

        playerManager = new ServerPlayerManager(playerName);
        physicsSystem = new PhysicsSystem(heightMap, playerManager);

        Player p = playerManager.getLocalPlayer();
        p.getPosition().x = 100;
        p.getPosition().y = heightMap.getBlock(100, 1)[0] + 500;
    }

    @Override
    public void create() {
        gameScreen = new GameScreen(heightMap, playerManager);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        physicsSystem.update(dt);
        gameScreen.render(dt);
    }
}
