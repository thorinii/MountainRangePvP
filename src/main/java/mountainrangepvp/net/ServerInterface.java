package mountainrangepvp.net;

/**
 * The interface the client talks to, either in-process or over the network.
 */
public interface ServerInterface {
    public void connect(ClientInterface client);

    public void login(ClientId client, int checkCode, int version, String nickname);

    public void shutdown();
}
