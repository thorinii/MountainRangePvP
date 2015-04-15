package mountainrangepvp.mp;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.util.Log;
import mountainrangepvp.world.chat.ChatLine;
import mountainrangepvp.world.chat.ChatListener;
import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.mp.message.KillConnectionMessage.Reason;
import mountainrangepvp.world.physics.PhysicsSystem;
import mountainrangepvp.world.player.ClientPlayerManager;
import mountainrangepvp.world.player.Player;
import mountainrangepvp.world.player.Player.Team;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ClientShotManager;
import mountainrangepvp.world.shot.Shot;
import mountainrangepvp.world.shot.ShotListener;
import mountainrangepvp.world.shot.ShotManager;
import mountainrangepvp.world.terrain.HeightMap;
import mountainrangepvp.world.terrain.HillsHeightMap;
import mountainrangepvp.world.terrain.Terrain;
import mountainrangepvp.util.Timer;

import java.io.IOException;

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
        messageClient.addMessageListener(world.getPlayerManager());
        messageClient.addMessageListener(world.getShotManager());
        messageClient.addMessageListener(world.getChatManager());

        world.getShotManager().addShotListener(new NewShotListener());
        world.getChatManager().addChatListener(new NewChatListener());
    }

    public void addMessageListener(MessageListener listener) {
        messageClient.addMessageListener(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageClient.removeMessageListener(listener);
    }

    public void start() throws IOException {
        messageClient.start();
    }

    public void update() {
        playerUpdateTimer.update();
        if (playerUpdateTimer.getTime() > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
            if (world.getPlayerManager().getLocalPlayer() != null) {
                PlayerUpdateMessage pum = new PlayerUpdateMessage(world.
                        getPlayerManager().getLocalPlayer());
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
                ClientPlayerManager cpm = (ClientPlayerManager) world.
                        getPlayerManager();

                IntroduceMessage introduceMessage = new IntroduceMessage(
                        cpm.getLocalPlayerName(), cpm.getLocalPlayerTeam());
                messageClient.send(introduceMessage);
            }
        }
    }

    private class NewShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            if (shot.player == world.getPlayerManager().getLocalPlayer())
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
            if (line.getPlayer() == world.getPlayerManager().
                    getLocalPlayer())
                messageClient.send(new NewChatMessage(line));
        }
    }

    public static void startClient(String host) throws IOException,
            InterruptedException {

        final GameWorld world = new GameWorld();

        String playerName = "test player " + (int) (Math.random() * 2000 + 2);
        PlayerManager playerManager = new ClientPlayerManager(playerName,
                                                              Team.GREEN);
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ClientShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        PhysicsSystem physicsSystem = new PhysicsSystem(world);

        GameClient client = new GameClient(world, host);
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

                    world.setTerrain(new Terrain(heightMap));
                    world.setTeamModeOn(newWorldMessage.isTeamModeOn());
                }
            }
        });

        client.start();

        Player local = null;
        while (client.isConnected()) {
            Thread.sleep(500 + (int) (Math.random() * 100));
            client.update();

            if (world.getTerrain() != null) {
                if (local == null)
                    local = playerManager.getLocalPlayer();

                local.getPosition().add(
                        (float) Math.random() * 300 - 100,
                        (float) Math.random() * 300 - 100);
                shotManager.addShot(local.getCentralPosition(), new Vector2(
                                            (float) Math.random() * 30 - 15,
                                            (float) Math.random() * 30 - 15).nor(),
                                    local);

                physicsSystem.update(1 / 60f);
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
