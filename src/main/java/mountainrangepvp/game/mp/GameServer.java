package mountainrangepvp.game.mp;

import mountainrangepvp.game.mp.message.*;
import mountainrangepvp.game.world.*;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.engine.util.Timer;

import java.io.IOException;

/**
 * @author lachlan
 */
public class GameServer {

    private final Session session;
    private final MessageServer messageServer;
    private final Timer playerUpdateTimer;
    private int seed;

    public GameServer(Session session) {
        this(session, MultiplayerConstants.STD_PORT);
    }

    public GameServer(Session session, int port) {
        this.session = session;
        this.messageServer = new MessageServer(port);
        playerUpdateTimer = new Timer();

        setup();
    }

    private void setup() {
        messageServer.addMessageListener(new GameServerMessageListener());
        messageServer.addMessageListener(session.playerManager);
        messageServer.addMessageListener(session.chatManager);

        session.chatManager.addChatListener(new NewChatListener());
    }

    public void addMessageListener(MessageListener listener) {
        messageServer.addMessageListener(listener);
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

    private Map lastMap = null;

    public void update() {
        if (session.hasMap() && session.getMap() != lastMap) {
            lastMap = session.getMap();
            messageServer.addMessageListener(lastMap.shotManager);
            lastMap.shotManager.addShotListener(new NewShotListener());
        }

        playerUpdateTimer.update();
        if (playerUpdateTimer.getTime() > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
            for (Player player : session.playerManager.getPlayers()) {
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

                Player existing = session.playerManager.getPlayer(introduceMessage.getName());
                if (existing != null) {
                    KillConnectionMessage kcm = new KillConnectionMessage(
                            KillConnectionMessage.Reason.DuplicatePlayer);
                    messageServer.send(kcm, id);
                } else {
                    NewWorldMessage newWorldMessage = new NewWorldMessage(
                            NewWorldMessage.WorldType.Hills, seed, session.getMap().teamModeOn);
                    messageServer.send(newWorldMessage, id);


                    for (Player player : session.playerManager.getPlayers()) {
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
        PlayerManager playerManager = new ServerPlayerManager();
        ChatManager chatManager = new ChatManager(playerManager);

        final Session session = new Session(playerManager, chatManager);

        Terrain terrain = new Terrain(new HillsHeightMap(seed));
        ShotManager shotManager = new ShotManager(playerManager, terrain, true, teamModeOn);

        Map map = new Map(shotManager, terrain, teamModeOn);
        session.setMap(map);

        final PhysicsSystem physicsSystem = new PhysicsSystem();

        final GameServer server = new GameServer(session);
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
                        physicsSystem.update(session, 1 / 60f);
                        session.update(1 / 60f);
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
