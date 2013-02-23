/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import mountainrangepvp.Log;
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

/**
 *
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

        world.getShotManager().addShotListener(new NewShotListener());
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

    private class GameServerMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof IntroduceMessage) {
                IntroduceMessage introduceMessage = (IntroduceMessage) message;

                Player existing = world.getPlayerManager().getPlayer(introduceMessage.
                        getName());
                if (existing != null) {
                    KillConnectionMessage kcm = new KillConnectionMessage(
                            KillConnectionMessage.Reason.DuplicatePlayer);
                    messageServer.send(kcm, id);
                } else {
                    NewWorldMessage newWorldMessage = new NewWorldMessage(
                            NewWorldMessage.WorldType.Hills, seed);
                    messageServer.send(newWorldMessage, id);


                    for (Player player : world.getPlayerManager().getPlayers()) {
                        PlayerConnectMessage pcm = new PlayerConnectMessage(
                                player);
                        messageServer.send(pcm, id);
                    }

                    messageServer.broadcastExcept(new PlayerConnectMessage(
                            new Player(introduceMessage.getName(), id)), id);
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

    public static void main(String[] args) throws IOException,
            InterruptedException {
        GameWorld world = new GameWorld();

        int seed = (int) (Math.random() * 100);

        Terrain terrain = new Terrain(new HillsHeightMap(seed));
        world.setTerrain(terrain);

        PlayerManager playerManager = new ServerPlayerManager();
        world.setPlayerManager(playerManager);

        ShotManager shotManager = new ServerShotManager(world);
        world.setShotManager(shotManager);

        PhysicsSystem physicsSystem = new PhysicsSystem(world);

        GameServer server = new GameServer(world);
        server.setSeed(seed);
        server.start();

        System.out.println("Test Server started");

        while (true) {
            Thread.sleep(1000 / 60);

            server.update();
            physicsSystem.update(1 / 60f);
            world.update(1 / 60f);
        }
    }
}
