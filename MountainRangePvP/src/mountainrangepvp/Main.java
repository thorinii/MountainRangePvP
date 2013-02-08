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
        Log.setupLog();
        Log.info("Startup");

        int option = JOptionPane.showOptionDialog(null,
                                                  "Start a server or connect to one?",
                                                  "Mountain Range PvP",
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE,
                                                  null,
                                                  new String[]{
                    "Server",
                    "Client"}, "Client");

        switch (option) {
            case 0:
                Log.info("Starting Server");
                startServer();
                break;
            case 1:
                Log.info("Starting Client");
                startClient();
                break;
        }
    }

    private static void startServer() {
        String playerName = JOptionPane.showInputDialog(null,
                                                        "What will you call yourself?",
                                                        "Mountain Range PvP",
                                                        JOptionPane.QUESTION_MESSAGE);

        startGame(new ServerGame(playerName, (int) (Math.random() * 100)));
    }

    private static void startClient() {
        String playerName = JOptionPane.showInputDialog(null,
                                                        "What will you call yourself?",
                                                        "Mountain Range PvP",
                                                        JOptionPane.QUESTION_MESSAGE);
        String serverIP = JOptionPane.showInputDialog(null,
                                                      "What's the server IP address?",
                                                      "Mountain Range PvP",
                                                      JOptionPane.QUESTION_MESSAGE);

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
