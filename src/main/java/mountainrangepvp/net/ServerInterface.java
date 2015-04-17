package mountainrangepvp.net;

/**
 * The interface the server responds to, either in-process or over the network.
 */
public interface ServerInterface {
    public void connect(int checkCode, int version, String nickname);
}
