package mountainrangepvp.net.server;

import mountainrangepvp.engine.util.Log;
import mountainrangepvp.net.ClientId;
import mountainrangepvp.net.ServerInterface;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The network-protocol agnostic thing that runs the world.
 */
public class Server implements ServerInterface {
    private final AtomicLong nextClientId = new AtomicLong(0L);

    @Override
    public ClientId connect() {
        ClientId id = new ClientId(nextClientId.getAndIncrement());
        Log.info(id + " connected");
        return id;
    }

    @Override
    public void login(ClientId client, int checkCode, int version, String nickname) {
        Log.info(client + ": " + checkCode + "," + version + " " + nickname + " connected");
    }
}
