package mountainrangepvp.net.server;

import mountainrangepvp.net.ServerInterface;

/**
 * Created by lachlan on 16/04/15.
 */
public class Server implements ServerInterface {
    @Override
    public void connect(int checkCode, int version, String nickname) {
        System.out.println(checkCode + "," + version + " " + nickname + " connected");
    }
}
