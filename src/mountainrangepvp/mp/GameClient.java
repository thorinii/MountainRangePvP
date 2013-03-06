/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import mountainrangepvp.Log;
import mountainrangepvp.chat.ChatLine;
import mountainrangepvp.chat.ChatListener;
import mountainrangepvp.chat.ChatManager;
import mountainrangepvp.game.GameWorld;
import mountainrangepvp.mp.message.KillConnectionMessage.Reason;
import mountainrangepvp.mp.message.*;
import static mountainrangepvp.mp.message.NewWorldMessage.WorldType.Hills;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.ClientPlayerManager;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.Player.Team;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.shot.ClientShotManager;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotListener;
import mountainrangepvp.shot.ShotManager;
import mountainrangepvp.terrain.HeightMap;
import mountainrangepvp.terrain.HillsHeightMap;
import mountainrangepvp.terrain.Terrain;
import mountainrangepvp.util.Timer;

/**
 *
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
            if (line.getPlayer() == world.getPlayerManager().getLocalPlayer())
                messageClient.send(new NewChatMessage(line));
        }
    }

    public static void main(String[] args) throws IOException,
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

        GameClient client = new GameClient(world, "localhost");
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
                }
            }
        });

        client.start();

        System.out.println("Test Client started");

        while (client.isConnected()) {
            Thread.sleep(100);
            client.update();

            if (world.getTerrain() != null) {
                physicsSystem.update(1 / 60f);
                world.update(1 / 60f);
            }
        }
    }
}
