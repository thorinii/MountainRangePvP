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
import mountainrangepvp.mp.Proxy;
import mountainrangepvp.mp.Server;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.ServerPlayerManager;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class ServerGame extends Game {

    private final HeightMap heightMap;
    //
    private final String playerName;
    private final Server server;
    //
    private final ServerPlayerManager playerManager;
    private final ShotManager shotManager;
    private final PhysicsSystem physicsSystem;
    private final InputHandler inputHandler;
    //
    private GameScreen gameScreen;

    public ServerGame(String playerName, int seed) {
        this.playerName = playerName;

        heightMap = new MountainHeightMap(seed);
        server = new Server(seed);

        playerManager = new ServerPlayerManager(playerName);
        shotManager = new ShotManager(heightMap, playerManager);
        physicsSystem = new PhysicsSystem(heightMap, playerManager);
        inputHandler = new InputHandler(playerManager, shotManager);
    }

    @Override
    public void create() {
        try {
            server.start();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error starting server",
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }

        inputHandler.register();

        gameScreen = new GameScreen(heightMap, playerManager, shotManager);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        server.update();

        inputHandler.update(dt);
        shotManager.update(dt);
        physicsSystem.update(dt);
        gameScreen.render(dt);
    }

    @Override
    public void dispose() {
        super.dispose();

        server.stop();
    }

    private class ServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, Proxy proxy) throws IOException {
            if (message instanceof HelloMessage) {
                server.sendPlayerConnect(playerName, proxy);
            } else if (message instanceof PlayerConnectMessage) {
                PlayerConnectMessage pcm = (PlayerConnectMessage) message;
                playerManager.addPlayer(pcm.getPlayerName());
            }
        }
    }
}
