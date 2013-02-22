/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.io.IOException;
import mountainrangepvp.game.GameWorld;
import mountainrangepvp.mp.message.*;
import mountainrangepvp.mp.message.KillConnectionMessage.Reason;

/**
 *
 * @author lachlan
 */
public class GameClient {

    private final GameWorld world;
    private final MessageClient messageClient;

    public GameClient(GameWorld world, String host) {
        this(world, host, MultiplayerConstants.STD_PORT);
    }

    public GameClient(GameWorld world, String host, int port) {
        this.world = world;
        messageClient = new MessageClient(host, port);

        setup();
    }

    private void setup() {
        messageClient.addMessageListener(new GameClientMessageListener());
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
        messageClient.update();
    }

    public void stop() {
        messageClient.send(new KillConnectionMessage(Reason.ClientExit));
        messageClient.update();
        messageClient.stop();
    }

    private class GameClientMessageListener implements MessageListener {

        @Override
        public void accept(Message message, int id) throws IOException {
            if (message instanceof ServerHelloMessage) {
                // TODO: fix this
                IntroduceMessage introduceMessage = new IntroduceMessage(
                        "Player");
                messageClient.send(introduceMessage);
            }
        }
    }
}
