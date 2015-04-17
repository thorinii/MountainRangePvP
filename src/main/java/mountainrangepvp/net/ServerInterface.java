package mountainrangepvp.net;

/**
 * The interface the server responds to, either in-process or over the network.
 */
public interface ServerInterface {
    public ClientId connect();

    public void login(ClientId client, int checkCode, int version, String nickname);
}
