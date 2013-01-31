/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import mountainrangepvp.player.ServerPlayerManager;

/**
 *
 * @author lachlan
 */
public class ServerGame extends Game {

    private final ServerPlayerManager playerManager;
    private final int seed;

    public ServerGame(String playerName, int seed) {
        playerManager = new ServerPlayerManager(playerName);

        this.seed = seed;
    }

    @Override
    public void create() {
        setScreen(new GameScreen(seed, playerManager));
    }
}
