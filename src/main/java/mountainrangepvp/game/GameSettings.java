package mountainrangepvp.game;

import mountainrangepvp.game.mp.MultiplayerConstants;
import mountainrangepvp.game.world.Player.Team;
import mountainrangepvp.net.NetworkConstants;

/**
 * @author lachlan
 */
public class GameSettings {
    public final int FPS = 60;
    public final float TIMESTEP = 1.0f / FPS;

    public boolean fullscreen;
    public int resolutionWidth, resolutionHeight;
    public int bitDepth;

    public String nickname;
    public Team team;
    public boolean teamModeOn;

    public boolean hosting;
    public int port;

    public String serverIP;

    public int seed;

    public boolean audioOn;

    public GameSettings() {
        fullscreen = false;
        resolutionWidth = 1000;
        resolutionHeight = 800;

        nickname = null;
        team = Team.BLUE;
        teamModeOn = false;

        hosting = false;
        port = NetworkConstants.DEFAULT_PORT;

        serverIP = "localhost";

        seed = 234;

        audioOn = false;
    }
}
