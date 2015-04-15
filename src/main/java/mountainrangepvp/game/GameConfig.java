package mountainrangepvp.game;

import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.world.player.Player.Team;

/**
 * @author lachlan
 */
public class GameConfig {
    public final int FPS = 60;
    public final float TIMESTEP = 1.0f / FPS;

    public boolean fullscreen;
    public int resolutionWidth, resolutionHeight;
    public int bitDepth;

    public String playerName;
    public Team team;
    public boolean teamModeOn;

    public boolean server;
    public int port;

    public String serverIP;

    public int seed;

    public boolean audioOn;

    public GameConfig() {
        fullscreen = false;
        resolutionWidth = 1000;
        resolutionHeight = 800;

        playerName = null;
        team = Team.BLUE;
        teamModeOn = false;

        server = false;
        port = MultiplayerConstants.STD_PORT;

        serverIP = "localhost";

        seed = 234;

        audioOn = false;
    }
}