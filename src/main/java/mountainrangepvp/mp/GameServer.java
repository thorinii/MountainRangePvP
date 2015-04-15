package mountainrangepvp.mp;

import mountainrangepvp.mp.message.*;
import mountainrangepvp.util.Log;
import mountainrangepvp.util.Timer;
import mountainrangepvp.world.GameWorld;
import mountainrangepvp.world.chat.ChatLine;
import mountainrangepvp.world.chat.ChatListener;
import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.physics.PhysicsSystem;
import mountainrangepvp.world.player.Player;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.player.ServerPlayerManager;
import mountainrangepvp.world.shot.ServerShotManager;
import mountainrangepvp.world.shot.Shot;
import mountainrangepvp.world.shot.ShotListener;
import mountainrangepvp.world.shot.ShotManager;
import mountainrangepvp.world.terrain.HillsHeightMap;
import mountainrangepvp.world.terrain.Terrain;

import java.io.IOException;

/**
 * @author lachlan
 */
public class GameServer {

    private final GameWorld world;
    private final MessageServer messageServer;
    private final Timer playerUpdateTimer;
    private int seed;

    public GameServer(GameWorld world) {
        this(world, MultiplayerConstants.STD_PORT);
    }

    public GameServer(GameWorld world, int port) {
        this.world = world;
        this.messageServer = new MessageServer(port);
        playerUpdateTimer = new Timer();

        setup();
    }

    private void setup() {
        messageServer.addMessageListener(new GameServerMessageListener());
        messageServer.addMessageListener(world.playerManager);
        messageServer.addMessageListener(world.shotManager);
        messageServer.addMessageListener(world.chatManager);

        world.shotManager.addShotListener(new NewShotListener());
        world.chatManager.addChatListener(new NewChatListener());
    }

    public void addMessageListener(MessageListener listener) {
        messageServer.addMessageListener(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageServer.removeMessageListener(listener);
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public void start() throws IOException {
        messageServer.start();
    }

    public void update() {
        playerUpdateTimer.update();
        if (playerUpdateTimer.getTime() > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
            for (Player player : world.playerManager.getPlayers()) {
                PlayerUpdateMessage pum = new PlayerUpdateMessage(player);
                messageServer.broadcastExcept(pum, player.getID());
            }

            playerUpdateTimer.reset();
        }

        messageServer.update();
    }

    public void stop() {
        messageServer.broadcast(new KillConnectionMessage(
                KillConnectionMessage.Reason.ServerShutdown));
        messageServer.update();
        messageServer.stop();
    }

    public boolean isGoing() {
        return messageServer.isGoing();
    }

    private class GameServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof IntroduceMessage) {
                IntroduceMessage introduceMessage = (IntroduceMessage) message;

                Player existing = world.playerManager.getPlayer(introduceMessage.getName());
                if (existing != null) {
                    KillConnectionMessage kcm = new KillConnectionMessage(
                            KillConnectionMessage.Reason.DuplicatePlayer);
                    messageServer.send(kcm, id);
                } else {
                    NewWorldMessage newWorldMessage = new NewWorldMessage(
                            NewWorldMessage.WorldType.Hills, seed, world.teamModeOn);
                    messageServer.send(newWorldMessage, id);


                    for (Player player : world.playerManager.getPlayers()) {
                        PlayerConnectMessage pcm = new PlayerConnectMessage(
                                player);
                        messageServer.send(pcm, id);
                    }

                    messageServer.broadcastExcept(new PlayerConnectMessage(
                            new Player(introduceMessage.getName(), id,
                                       introduceMessage.getTeam())), id);
                }

            } else if (message instanceof KillConnectionMessage) {
                messageServer.broadcastExcept(new PlayerDisconnectMessage(id),
                                              id);

            } else if (message instanceof NewShotMessage) {
                messageServer.broadcastExcept(message, id);

            }
        }
    }

    private class NewShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
        }

        @Override
        public void shotTerrainCollision(Shot shot) {
        }

        @Override
        public void shotPlayerCollision(Shot shot, Player player) {
            messageServer.broadcast(new PlayerDeathMessage(player, shot.player));
        }
    }

    private class NewChatListener implements ChatListener {

        @Override
        public void onMessage(ChatLine line) {
            if (line.isServer())
                messageServer.broadcast(new NewChatMessage(line));
            else
                messageServer.broadcastExcept(new NewChatMessage(line),
                                              line.
                                                      getPlayer().getID());
        }
    }

    public static GameServer startBasicServer(int seed, boolean teamModeOn) throws IOException {
        Terrain terrain = new Terrain(new HillsHeightMap(seed));

        PlayerManager playerManager = new ServerPlayerManager();

        ServerShotManager shotManager = new ServerShotManager();

        ChatManager chatManager = new ChatManager(playerManager);

        final GameWorld world = new GameWorld(playerManager, shotManager, chatManager, terrain, teamModeOn);
        shotManager.setWorld(world);

        final PhysicsSystem physicsSystem = new PhysicsSystem();

        final GameServer server = new GameServer(world);
        server.setSeed(seed);

        Log.info("Starting server...");
        server.start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.info("Server ready");

                    while (server.isGoing()) {
                        Thread.sleep(1000 / 60);

                        server.update();
                        physicsSystem.update(world, 1 / 60f);
                        world.update(1 / 60f);
                    }
                } catch (Exception e) {
                    Log.warn("Server crashed:", e);
                }

                System.out.println("Server stopped");
            }
        }).start();

        return server;
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        int seed = (int) (Math.random() * 100);

        startBasicServer(seed, false);
    }
}
