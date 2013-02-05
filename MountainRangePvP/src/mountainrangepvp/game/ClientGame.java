/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import javax.swing.JOptionPane;
import mountainrangepvp.generator.HeightMap;
import mountainrangepvp.generator.MountainHeightMap;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.Client;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.ServerPlayerManager;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class ClientGame extends Game {

    private HeightMap heightMap;
    private Client client;
    private final String playerName;
    private final String serverIP;
    private ServerPlayerManager playerManager;
    private ShotManager shotManager;
    private PhysicsSystem physicsSystem;
    private InputHandler inputHandler;
    private GameScreen gameScreen;

    public ClientGame(String playerName, String serverIP) {
        this.playerName = playerName;
        this.serverIP = serverIP;
    }

    @Override
    public void create() {
        try {
            client = new Client(serverIP, MultiplayerConstants.STD_PORT);
            client.start();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error starting server",
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }

        heightMap = new MountainHeightMap(client.getSeed());
        playerManager = new ServerPlayerManager(playerName);
        shotManager = new ShotManager(heightMap, playerManager);
        physicsSystem = new PhysicsSystem(heightMap, playerManager);
        inputHandler = new InputHandler(playerManager, shotManager);

        inputHandler.register();

        gameScreen = new GameScreen(heightMap, playerManager, shotManager);
        setScreen(gameScreen);
    }
}
