/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.player.Player.Team;

/**
 *
 * @author lachlan
 */
public class GameConfig {

    public boolean fullscreen;
    public int resolutionWidth, resolutionHeight;
    public int bitDepth;
    //
    public String playerName;
    public Team team;
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