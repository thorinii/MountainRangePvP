/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import mountainrangepvp.game.GameWorld;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.PlayerManager;
import mountainrangepvp.player.ServerPlayerManager;
import mountainrangepvp.util.Timer;

/**
 *
 * @author lachlan
 */
public class GameServer {

    private final GameWorld world;
    private final MessageServer messageServer;
    private final Timer playerUpdateTimer;

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
    }

    public void addMessageListener(MessageListener listener) {
        messageServer.addMessageListener(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageServer.removeMessageListener(listener);
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
                            NewWorldMessage.WorldType.Hills, 12);
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

            }
        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        PlayerManager manager = new ServerPlayerManager();

        GameWorld world = new GameWorld();
        world.setPlayerManager(manager);

        GameServer server = new GameServer(world);
        server.start();

        System.out.println("Test Server started");

        while (true) {
            Thread.sleep(1000 / 60);

            server.update();
            world.update(1 / 60f);
        }
    }
}
