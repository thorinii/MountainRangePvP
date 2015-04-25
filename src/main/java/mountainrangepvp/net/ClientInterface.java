package mountainrangepvp.net;

/**
 * The interface the server talks to, either in-process or over the network.
 */
public interface ClientInterface {
    /**
     * Tells a client that they've successfully connected.
     */
    public void connected(ClientId id);

    public void sessionInfo(boolean teamsOn);

    public void newMap(int seed);
}
