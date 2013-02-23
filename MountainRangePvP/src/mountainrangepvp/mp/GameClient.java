/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import mountainrangepvp.game.GameWorld;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.mp.message.KillConnectionMessage.Reason;
import mountainrangepvp.player.ClientPlayerManager;
import mountainrangepvp.player.PlayerManager;
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
                // TODO: fix this
                IntroduceMessage introduceMessage = new IntroduceMessage(
                        ((ClientPlayerManager) world.getPlayerManager()).
                        getLocalPlayerName());
                messageClient.send(introduceMessage);
            }
        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        String playerName = "test player "
                + (int) (Math.random() * 2000 + 2);

        PlayerManager manager = new ClientPlayerManager(playerName);

        GameWorld world = new GameWorld();
        world.setPlayerManager(manager);

        GameClient client = new GameClient(world, "localhost");
        client.start();

        System.out.println("Test Server started");

        while (client.isConnected()) {
            Thread.sleep(100);
            client.update();
        }
    }
}
