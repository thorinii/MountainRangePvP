package mountainrangepvp.mp;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.mp.message.KillConnectionMessage.Reason;
import mountainrangepvp.util.Log;
import mountainrangepvp.util.Timer;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.world.chat.ChatLine;
import mountainrangepvp.world.chat.ChatListener;
import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.physics.PhysicsSystem;
import mountainrangepvp.world.player.ClientPlayerManager;
import mountainrangepvp.world.player.Player;
import mountainrangepvp.world.player.Player.Team;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ClientShotManager;
import mountainrangepvp.world.shot.Shot;
import mountainrangepvp.world.shot.ShotListener;
import mountainrangepvp.world.terrain.HeightMap;
import mountainrangepvp.world.terrain.HillsHeightMap;
import mountainrangepvp.world.terrain.Terrain;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lachlan
 */
public class GameClient {

    private final GameWorld world;
    private final MessageClient messageClient;
    private final Timer playerUpdateTimer;

    public GameClient(GameWorld world, String host) {
        this(world, host, MultiplayerConstants.STD_PORT);
    }

    public GameClient(GameWorld world, String host, int port) {
        this.world = world;
        messageClient = new MessageClient(host, port);
        playerUpdateTimer = new Timer();

        setup();
    }

    private void setup() {
        messageClient.addMessageListener(new GameClientMessageListener());
        messageClient.addMessageListener(world.playerManager);
        messageClient.addMessageListener(world.shotManager);
        messageClient.addMessageListener(world.chatManager);

        world.shotManager.addShotListener(new NewShotListener());
        world.chatManager.addChatListener(new NewChatListener());
    }

    public void addMessageListener(MessageListener listener) {
        messageClient.addMessageListener(listener);
    }

    public void start() throws IOException {
        messageClient.start();
    }

    public void update() {
        playerUpdateTimer.update();
        if (playerUpdateTimer.getTime() > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
            if (world.playerManager.getLocalPlayer() != null) {
                PlayerUpdateMessage pum = new PlayerUpdateMessage(world.playerManager.getLocalPlayer());
                messageClient.send(pum);

                playerUpdateTimer.reset();
            }
        }

        messageClient.update();
    }

    public void stop() {
        messageClient.send(new KillConnectionMessage(Reason.ClientExit));
        messageClient.update();
        messageClient.stop();
    }

    public boolean isConnected() {
        return messageClient.isConnected();
    }

    private class GameClientMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof ServerHelloMessage) {
                ClientPlayerManager cpm = (ClientPlayerManager) world.playerManager;

                IntroduceMessage introduceMessage = new IntroduceMessage(
                        cpm.getLocalPlayerName(), cpm.getLocalPlayerTeam());
                messageClient.send(introduceMessage);
            }
        }
    }

    private class NewShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            if (shot.player == world.playerManager.getLocalPlayer())
                messageClient.send(new NewShotMessage(shot));
        }

        @Override
        public void shotTerrainCollision(Shot shot) {
        }

        @Override
        public void shotPlayerCollision(Shot shot, Player player) {
        }
    }

    private class NewChatListener implements ChatListener {

        @Override
        public void onMessage(ChatLine line) {
            if (line.getPlayer() == world.playerManager.getLocalPlayer())
                messageClient.send(new NewChatMessage(line));
        }
    }

    public static void startClient(String host) throws IOException,
            InterruptedException {

        String playerName = "test player " + (int) (Math.random() * 2000 + 2);
        final PlayerManager playerManager = new ClientPlayerManager(playerName, Team.GREEN);
        final ClientShotManager shotManager = new ClientShotManager();
        final ChatManager chatManager = new ChatManager(playerManager);
        PhysicsSystem physicsSystem = new PhysicsSystem();

        final AtomicReference<GameWorld> worldRef = new AtomicReference<>();

        if (true)
            throw new UnsupportedOperationException("Broken implementation");
        GameClient client = new GameClient(null, host);
        client.addMessageListener(new MessageListener() {
            @Override
            public void accept(Message message, int id) throws IOException {
                if (message instanceof NewWorldMessage) {
                    NewWorldMessage newWorldMessage = (NewWorldMessage) message;

                    Log.info("Received Seed", newWorldMessage.getSeed(),
                             "Changing Map");

                    HeightMap heightMap;
                    switch (newWorldMessage.getWorldType()) {
                        case Hills:
                            heightMap = new HillsHeightMap(newWorldMessage.
                                    getSeed());
                            break;
                        default:
                            heightMap = null;
                    }

                    GameWorld world = new GameWorld(playerManager, shotManager, chatManager, new Terrain(heightMap), newWorldMessage.isTeamModeOn());
                    shotManager.setWorld(world);
                    worldRef.set(world);
                }
            }
        });

        client.start();

        Player local = null;
        while (client.isConnected()) {
            GameWorld world = worldRef.get();

            Thread.sleep(500 + (int) (Math.random() * 100));
            client.update();

            if (world.terrain != null) {
                if (local == null)
                    local = playerManager.getLocalPlayer();

                local.getPosition().add(
                        (float) Math.random() * 300 - 100,
                        (float) Math.random() * 300 - 100);
                shotManager.addShot(local.getCentralPosition(), new Vector2(
                                            (float) Math.random() * 30 - 15,
                                            (float) Math.random() * 30 - 15).nor(),
                                    local);

                physicsSystem.update(world, 1 / 60f);
                world.update(1 / 60f);
            }

        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        System.out.println("Test Client starting");

        if (args.length == 1)
            startClient(args[0]);
        else
            startClient("localhost");
    }
}
