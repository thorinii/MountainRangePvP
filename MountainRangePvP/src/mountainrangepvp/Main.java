/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import javax.swing.JOptionPane;
import mountainrangepvp.game.ClientGame;
import mountainrangepvp.game.ServerGame;

/**
 *
 * @author lachlan
 */
public class Main {

    public static void main(String[] args) {
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
                startServer();
                break;
            case 1:
                startClient();
                break;
        }
    }

    private static void startServer() {
        String playerName = JOptionPane.showInputDialog(null,
                                                        "What will you call yourself?",
                                                        "Mountain Range PvP",
                                                        JOptionPane.QUESTION_MESSAGE);

        startGame(new ServerGame(playerName, 99));
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
