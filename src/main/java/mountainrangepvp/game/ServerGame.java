package mountainrangepvp.game;

import mountainrangepvp.mp.GameServer;
import mountainrangepvp.util.Log;

import java.io.IOException;

/**
 * @author lachlan
 */
public class ServerGame extends Game {

    private GameServer server;
    private final int seed;

    public ServerGame(GameConfig config) {
        super(config);
        this.seed = config.seed;
    }

    @Override
    public void start() {
        try {
            server = GameServer.startBasicServer(seed, world.teamModeOn);
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
