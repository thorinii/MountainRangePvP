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
import mountainrangepvp.mp.Proxy;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.ServerPlayerManager;
import mountainrangepvp.shot.ShotManager;

/**
 *
 * @author lachlan
 */
public class ClientGame extends Game {

    private HeightMap heightMap;
    //
    private final String playerName;
    private final String serverIP;
    private Client client;
    //
    private ServerPlayerManager playerManager;
    private ShotManager shotManager;
    private PhysicsSystem physicsSystem;
    private InputHandler inputHandler;
    //
    private GameScreen gameScreen;

    public ClientGame(String playerName, String serverIP) {
        this.playerName = playerName;
        this.serverIP = serverIP;
    }

    @Override
    public void create() {
        try {
            client = new Client(serverIP, MultiplayerConstants.STD_PORT);
            client.getMessageQueue().addListener(new ServerMessageListener());
            client.start();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error starting client " + ioe,
                                          "Mountain Range PvP",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        client.update();

        if (gameScreen != null) {
            inputHandler.update(dt);
            shotManager.update(dt);
            physicsSystem.update(dt);
            gameScreen.render(dt);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        client.stop();
    }

    private class ServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, Proxy proxy) throws IOException {
            if (message instanceof SeedMessage) {
                SeedMessage seedMessage = (SeedMessage) message;

                heightMap = new MountainHeightMap(seedMessage.getSeed());
                playerManager = new ServerPlayerManager(playerName);
                shotManager = new ShotManager(heightMap, playerManager);
                physicsSystem = new PhysicsSystem(heightMap, playerManager);

                inputHandler = new InputHandler(playerManager, shotManager);
                inputHandler.register();

                gameScreen = new GameScreen(heightMap, playerManager,
                                            shotManager);
                setScreen(gameScreen);
            } else if (message instanceof HelloMessage) {
                client.sendPlayerConnect(playerName);
            } else if (message instanceof PlayerConnectMessage) {
                PlayerConnectMessage pcm = (PlayerConnectMessage) message;
                playerManager.addPlayer(pcm.getPlayerName());
            }
        }
    }
}
