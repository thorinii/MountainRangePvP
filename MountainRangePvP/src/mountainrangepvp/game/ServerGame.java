/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;

/**
 *
 * @author lachlan
 */
public class ServerGame extends Game {

    private final int seed;

    public ServerGame(String playerName, int seed) {
        this.seed = seed;
    }

    @Override
    public void create() {
        setScreen(new GameScreen(seed));
    }
}
