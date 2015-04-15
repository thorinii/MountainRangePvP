package mountainrangepvp.game;

import mountainrangepvp.audio.AudioManager;
import mountainrangepvp.input.InputHandler;
import mountainrangepvp.mp.message.KillConnectionMessage;
import mountainrangepvp.mp.message.Message;
import mountainrangepvp.mp.message.NewWorldMessage;
import mountainrangepvp.renderer.GameScreen;
import mountainrangepvp.util.Log;
import mountainrangepvp.world.physics.PhysicsSystem;
import mountainrangepvp.world.terrain.HeightMap;
import mountainrangepvp.world.terrain.HillsHeightMap;
import mountainrangepvp.world.terrain.Terrain;

import java.io.IOException;

/**
 * @author lachlan
 */
public class ClientGame extends Game {

    public ClientGame(GameConfig config) {
        super(config);
    }

    @Override
    public void accept(Message message, int id) throws IOException {
        if (message instanceof KillConnectionMessage) {
            KillConnectionMessage kill = (KillConnectionMessage) message;

            Log.crash("Server disconnected: " + kill.getReason());
        } else if (message instanceof NewWorldMessage) {
            NewWorldMessage newWorldMessage = (NewWorldMessage) message;

            Log.info("Received Seed", newWorldMessage.getSeed(),
                     "Changing Map");

            HeightMap heightMap;
            switch (newWorldMessage.getWorldType()) {
                case Hills:
                    heightMap = new HillsHeightMap(newWorldMessage.getSeed());
                    break;
                default:
                    heightMap = null;
            }

            world.setTerrain(new Terrain(heightMap));
            world.setTeamModeOn(newWorldMessage.isTeamModeOn());

            physicsSystem = new PhysicsSystem(world);

            inputHandler = new InputHandler(world);
            inputHandler.register();

            audioManager = new AudioManager(world.getPlayerManager(),
                                            world.getShotManager(),
                                            config);
            audioManager.loadAudio();

            gameScreen = new GameScreen(world);
        }
    }
}
