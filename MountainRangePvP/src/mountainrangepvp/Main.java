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

    public static void startServer(boolean fullscreen, String resolution,
            String playerName) {
        startGame(fullscreen, resolution, new ServerGame(playerName, 11));
    }

    public static void startClient(boolean fullscreen, String resolution,
            String playerName, String serverIP) {
        startGame(fullscreen, resolution, new ClientGame(playerName, serverIP));
    }

    private static void startGame(boolean fullscreen, String resolution,
            Game game) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        String[] res = resolution.split("x");

        config.title = "Mountain Range PvP";
        config.useGL20 = true;
        config.forceExit = false;
        config.fullscreen = fullscreen;

        if (fullscreen) {
            config.width = Integer.parseInt(res[0]);
            config.height = Integer.parseInt(res[1].split(" ")[0]);
        } else {
            config.width = 1000;
            config.height = 800;
        }

        LwjglApplication app = new LwjglApplication(game, config);
    }
}
