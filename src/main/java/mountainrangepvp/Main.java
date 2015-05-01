package mountainrangepvp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.game.Game;
import mountainrangepvp.game.GameSettings;
import mountainrangepvp.net.server.Server;
import mountainrangepvp.net.server.ServerThread;
import mountainrangepvp.net.server.SessionConfig;
import mountainrangepvp.net.tcp.TcpServerInterface;
import mountainrangepvp.net.tcp.TcpServerWrapper;

/**
 * @author lachlan
 */
public class Main {

    private static String USAGE = "Usage:\n" +
            "\tjava -jar mountainrangepvp.jar\n" +
            "\tjava -jar mountainrangepvp.jar client <server ip> <username>\n" +
            "\tjava -jar mountainrangepvp.jar server <username>\n" +
            "\tjava -jar mountainrangepvp.jar stress <server ip> <number of clients>";

    private static void printUsageAndQuit() {
        System.err.println(USAGE);
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            GameSettings settings = new GameSettings();

            switch (args[0]) {
                case "client":
                    if (args.length == 3) {
                        settings.hosting = false;
                        settings.serverIP = args[1];
                        settings.nickname = args[2];
                    } else
                        printUsageAndQuit();
                    break;

                case "server":
                    if (args.length == 2) {
                        settings.hosting = true;
                        settings.nickname = args[1];
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

            new Main(settings).startGame();
        } else {
            LauncherGUI.laf();

            LauncherGUI launcher = new LauncherGUI();
            launcher.setVisible(true);
        }
    }


    private final GameSettings settings;

    public Main(GameSettings settings) {
        this.settings = settings;
    }

    public void startGame() {
        LwjglApplicationConfiguration appConfig = new LwjglApplicationConfiguration();
        appConfig.title = "Mountain Range PvP";
        appConfig.forceExit = false;
        appConfig.fullscreen = settings.fullscreen;
        appConfig.resizable = false;
        appConfig.vSyncEnabled = true;

        appConfig.width = settings.resolutionWidth;
        appConfig.height = settings.resolutionHeight;
        appConfig.depth = settings.bitDepth;
        //appConfig.forceExit = true;

        LwjglApplication app = new LwjglApplication(gameAdapter(), appConfig);
    }

    private ApplicationListener gameAdapter() {
        if (settings.hosting)
            return hostingAdapter();
        else
            return joiningAdapter();
    }


    private ApplicationListener hostingAdapter() {
        return new ApplicationAdapter() {
            TcpServerWrapper wrapper;
            Game game;

            @Override
            public void create() {
                SessionConfig sessionConfig = new SessionConfig(settings.teamsOn);

                Server server = ServerThread.startServer(new Log("server"), sessionConfig);
                wrapper = new TcpServerWrapper(new Log("tcp"), server, settings.port);
                wrapper.start();

                game = new Game(settings, server);
                game.start();
            }

            @Override
            public void render() {
                game.render();
            }

            @Override
            public void dispose() {
                game.kill();
                wrapper.shutdown();
            }
        };
    }

    private ApplicationListener joiningAdapter() {
        return new ApplicationAdapter() {
            TcpServerInterface server;
            Game game;

            @Override
            public void create() {
                server = new TcpServerInterface(new Log("tcp"), settings.serverIP, settings.port);

                game = new Game(settings, server);
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
}
