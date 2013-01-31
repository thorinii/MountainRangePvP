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
public class ClientGame extends Game {

    private final int seed;

    public ClientGame(String playerName, int seed, String serverIP) {
        this.seed = seed;
    }

    @Override
    public void create() {
        setScreen(new GameScreen(seed, null));
    }
}
