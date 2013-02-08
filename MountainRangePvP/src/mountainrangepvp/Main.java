/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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

    public static void startServer(String playerName) {
        startGame(new ServerGame(playerName, (int) (Math.random() * 100) + 1));
    }

    public static void startClient(String playerName, String serverIP) {
        startGame(new ClientGame(playerName, serverIP));
    }

    private static void startGame(Game game) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "Mountain Range PvP";
        config.width = 800;
        config.height = 600;
        config.useGL20 = true;
        config.forceExit = false;

        LwjglApplication app = new LwjglApplication(game, config);
    }
}
