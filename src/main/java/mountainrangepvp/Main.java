package mountainrangepvp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import mountainrangepvp.game.ClientGame;
import mountainrangepvp.game.Game;
import mountainrangepvp.game.ServerGame;
import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.net.Server;

/**
 * @author lachlan
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            GameSettings config = new GameSettings();

            switch (args[0]) {
                case "client":
                    if (args.length == 3) {
                        config.server = false;
                        config.serverIP = args[1];
                        config.playerName = args[2];
                    } else
                        printUsageAndQuit();
                    break;

                case "server":
                    if (args.length == 2) {
                        config.server = true;
                        config.playerName = args[1];
                    } else
                        printUsageAndQuit();
                    break;

                case "stress":
                    if (args.length == 3) {
                        StressTest.stressTest(args[1], Integer.parseInt(args[2]));
                        return;
                    } else
                        printUsageAndQuit();
                    break;

                default:
                    printUsageAndQuit();
                    break;
            }

            startGame(config);
        } else {
            LauncherGUI.laf();

            LauncherGUI launcher = new LauncherGUI();
            launcher.setVisible(true);
        }
    }

    public static void startGame(GameSettings gameSettings) {
        LwjglApplicationConfiguration appConfig = new LwjglApplicationConfiguration();
        appConfig.title = "Mountain Range PvP";
        appConfig.forceExit = false;
        appConfig.fullscreen = gameSettings.fullscreen;
        appConfig.resizable = false;
        appConfig.vSyncEnabled = true;

        appConfig.width = gameSettings.resolutionWidth;
        appConfig.height = gameSettings.resolutionHeight;
        appConfig.depth = gameSettings.bitDepth;

        LwjglApplication app = new LwjglApplication(gameListenerAdaptor(gameSettings), appConfig);
    }

    private static ApplicationListener gameListenerAdaptor(final GameSettings config) {
        return new ApplicationAdapter() {
            Game game;

            @Override
            public void create() {
                if (config.server) {
                    game = new ServerGame(config, new Server());
                } else {
                    game = new ClientGame(config, null);
                }

                game.start();
            }

            @Override
            public void render() {
                game.render();
            }

            @Override
            public void dispose() {
                game.kill();
            }
        };
    }

    private static void printUsageAndQuit() {
        System.err.println(USAGE);
        System.exit(0);
    }

    private static String USAGE = "Usage:\n" +
            "\tjava -jar mountainrangepvp.jar\n" +
            "\tjava -jar mountainrangepvp.jar client <server ip> <username>\n" +
            "\tjava -jar mountainrangepvp.jar server <username>\n" +
            "\tjava -jar mountainrangepvp.jar stress <server ip> <number of clients>";
}
