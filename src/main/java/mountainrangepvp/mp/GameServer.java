package mountainrangepvp.mp;

import mountainrangepvp.Log;
import mountainrangepvp.chat.ChatLine;
import mountainrangepvp.chat.ChatListener;
import mountainrangepvp.chat.ChatManager;
import mountainrangepvp.game.GameWorld;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.physics.PhysicsSystem;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.player.ServerPlayerManager;
import mountainrangepvp.shot.ServerShotManager;
import mountainrangepvp.shot.Shot;
import mountainrangepvp.shot.ShotListener;
import mountainrangepvp.shot.ShotManager;
import mountainrangepvp.terrain.HillsHeightMap;
import mountainrangepvp.terrain.Terrain;
import mountainrangepvp.util.Timer;

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
        messageServer.addMessageListener(world.getPlayerManager());
        messageServer.addMessageListener(world.getShotManager());
        messageServer.addMessageListener(world.getChatManager());

        world.getShotManager().addShotListener(new NewShotListener());
        world.getChatManager().addChatListener(new NewChatListener());
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
            for (Player player : world.getPlayerManager().getPlayers()) {
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

                Player existing = world.getPlayerManager().getPlayer(
                        introduceMessage.
                                getName());
                if (existing != null) {
                    KillConnectionMessage kcm = new KillConnectionMessage(
                            KillConnectionMessage.Reason.DuplicatePlayer);
                    messageServer.send(kcm, id);
                } else {
                    NewWorldMessage newWorldMessage = new NewWorldMessage(
                            NewWorldMessage.WorldType.Hills, seed, world.
                            isTeamModeOn());
                    messageServer.send(newWorldMessage, id);


                    for (Player player : world.getPlayerManager().getPlayers()) {
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

    public static GameServer startBasicServer(int seed, boolean teamModeOn) {
        final GameWorld world = new GameWorld();
        world.setTeamModeOn(teamModeOn);

        Terrain terrain = new Terrain(new HillsHeightMap(seed));
        world.setTerrain(terrain);

        PlayerManager playerManager = new ServerPlayerManager();
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ServerShotManager(world);
        world.setShotManager(shotManager);

        ChatManager chatManager = new ChatManager(playerManager);
        world.setChatManager(chatManager);

        final PhysicsSystem physicsSystem = new PhysicsSystem(world);

        final GameServer server = new GameServer(world);
        server.setSeed(seed);

        try {
            Log.info("Starting server...");
            server.start();
        } catch (IOException ex) {
            Log.warn("Server could not start:", ex);
            return null;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.info("Server ready");

                    while (server.isGoing()) {
                        Thread.sleep(1000 / 60);

                        server.update();
                        physicsSystem.update(1 / 60f);
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
