/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import mountainrangepvp.game.ClientGame;
import mountainrangepvp.game.ServerGame;

/**
 *
 * @author lachlan
 */
public class Main {

    public static void main(String[] args) {
        Log.info("Startup");
        LauncherGUI.laf();

        LauncherGUI launcher = new LauncherGUI();
        launcher.setVisible(true);
    }

    public static void startGame(GameConfig gameConfig) {
        Game game;
        if (gameConfig.server) {
            game = new ServerGame(gameConfig.playerName, gameConfig.seed);
        } else {
            game = new ClientGame(gameConfig.playerName, gameConfig.serverIP);
        }

        LwjglApplicationConfiguration appConfig = new LwjglApplicationConfiguration();
        appConfig.title = "Mountain Range PvP";
        appConfig.useGL20 = true;
        appConfig.forceExit = false;
        appConfig.fullscreen = gameConfig.fullscreen;
        appConfig.width = gameConfig.resolutionWidth;
        appConfig.height = gameConfig.resolutionHeight;

        LwjglApplication app = new LwjglApplication(game, appConfig);
    }
}
