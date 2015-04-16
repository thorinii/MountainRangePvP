package mountainrangepvp.game;

import mountainrangepvp.engine.util.Log;
import mountainrangepvp.game.mp.GameServer;
import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.net.ServerInterface;

import java.io.IOException;

/**
 * @author lachlan
 */
public class ServerGame extends Game {

    private GameServer server;
    private final int seed;

    public ServerGame(GameSettings config, ServerInterface server) {
        super(config, server);
        this.seed = config.seed;
    }

    @Override
    public void start() {
        try {
            server = GameServer.startBasicServer(seed, config.teamModeOn);
        } catch (IOException ioe) {
            Log.crash("Could not start server", ioe);
        }

        super.start();
    }

    @Override
    public void kill() {
        super.kill();
        server.stop();
    }
}
