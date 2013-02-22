/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import mountainrangepvp.game.GameWorld;
import mountainrangepvp.mp.message.*;

/**
 *
 * @author lachlan
 */
public class GameServer {

    private final GameWorld world;
    private final MessageServer messageServer;

    public GameServer(GameWorld world) {
        this(world, MultiplayerConstants.STD_PORT);
    }

    public GameServer(GameWorld world, int port) {
        this.world = world;
        this.messageServer = new MessageServer(port);

        setup();
    }

    private void setup() {
        messageServer.addMessageListener(new GameServerMessageListener());
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
                NewWorldMessage newWorldMessage = new NewWorldMessage(
                        NewWorldMessage.WorldType.Hills, 12);
                messageServer.send(newWorldMessage, id);
            } else {
                System.out.println(id + " sent " + message);
            }
        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        GameServer server = new GameServer(null);
        server.start();

        System.out.println("Test Server started");

        while (true) {
            Thread.sleep(100);
            server.update();
        }
    }
}
