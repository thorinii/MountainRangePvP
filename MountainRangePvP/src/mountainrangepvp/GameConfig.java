/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import mountainrangepvp.mp.MultiplayerConstants;

/**
 *
 * @author lachlan
 */
public class GameConfig {

    public boolean fullscreen;
    public int resolutionWidth, resolutionHeight;
    //
    public String playerName;
    //
    public boolean server;
    public int port;
    //
    public String serverIP;
    //
    public int seed;

    public GameConfig() {
        fullscreen = false;
        resolutionWidth = 1000;
        resolutionHeight = 800;

        playerName = null;

        server = false;
        port = MultiplayerConstants.STD_PORT;

        serverIP = "localhost";

        seed = 11;
    }
}
