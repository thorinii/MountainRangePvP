package mountainrangepvp.game.mp;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.game.mp.message.*;
import mountainrangepvp.game.mp.message.KillConnectionMessage.Reason;
import mountainrangepvp.game.world.*;
import mountainrangepvp.game.world.Player.Team;
import mountainrangepvp.engine.util.Log;
import mountainrangepvp.engine.util.Timer;

import java.io.IOException;

/**
 * @author lachlan
 */
public class GameClient {

    private final Instance instance;
    private final MessageClient messageClient;
    private final Timer playerUpdateTimer;

    public GameClient(Instance instance, String host) {
        this(instance, host, MultiplayerConstants.STD_PORT);
    }

    public GameClient(Instance instance, String host, int port) {
        this.instance = instance;
        messageClient = new MessageClient(host, port);
        playerUpdateTimer = new Timer();

        setup();
    }

    private void setup() {
        messageClient.addMessageListener(new GameClientMessageListener());
        messageClient.addMessageListener(instance.playerManager);
        messageClient.addMessageListener(instance.chatManager);

        instance.chatManager.addChatListener(new NewChatListener());
    }

    public void addMessageListener(MessageListener listener) {
        messageClient.addMessageListener(listener);
    }

    public void start() throws IOException {
        messageClient.start();
    }

    private Map lastMap = null;

    public void update() {
        if (instance.hasMap() && instance.getMap() != lastMap) {
            lastMap = instance.getMap();
            messageClient.addMessageListener(lastMap.shotManager);
            lastMap.shotManager.addShotListener(new NewShotListener());
        }

        playerUpdateTimer.update();
        if (playerUpdateTimer.getTime() > MultiplayerConstants.PLAYER_UPDATE_TIMER) {
            if (instance.playerManager.getLocalPlayer() != null) {
                PlayerUpdateMessage pum = new PlayerUpdateMessage(instance.playerManager.getLocalPlayer());
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
                ClientPlayerManager cpm = (ClientPlayerManager) instance.playerManager;

                IntroduceMessage introduceMessage = new IntroduceMessage(
                        cpm.getLocalPlayerName(), cpm.getLocalPlayerTeam());
                messageClient.send(introduceMessage);
            }
        }
    }

    private class NewShotListener implements ShotListener {

        @Override
        public void shotAdd(Shot shot) {
            if (shot.player == instance.playerManager.getLocalPlayer())
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
            if (line.getPlayer() == instance.playerManager.getLocalPlayer())
                messageClient.send(new NewChatMessage(line));
        }
    }

    public static void startClient(String host) throws IOException,
            InterruptedException {

        String playerName = "test player " + (int) (Math.random() * 2000 + 2);
        final PlayerManager playerManager = new ClientPlayerManager(playerName, Team.GREEN);
        final ChatManager chatManager = new ChatManager(playerManager);
        PhysicsSystem physicsSystem = new PhysicsSystem();

        final Instance instance = new Instance(playerManager, chatManager);

        GameClient client = new GameClient(instance, host);
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

                    Terrain terrain = new Terrain(heightMap);
                    ShotManager shotManager = new ShotManager(playerManager, terrain, false, newWorldMessage.isTeamModeOn());
                    Map map = new Map(shotManager, terrain, newWorldMessage.isTeamModeOn());
                    instance.setMap(map);
                }
            }
        });

        client.start();

        Player local = null;
        while (client.isConnected()) {
            Thread.sleep(500 + (int) (Math.random() * 100));
            client.update();

            if (instance.hasMap()) {
                if (local == null)
                    local = playerManager.getLocalPlayer();

                local.getPosition().add(
                        (float) Math.random() * 300 - 100,
                        (float) Math.random() * 300 - 100);
                instance.getMap().shotManager.addShot(local.getCentralPosition(), new Vector2(
                                                              (float) Math.random() * 30 - 15,
                                                              (float) Math.random() * 30 - 15).nor(),
                                                      local);

                physicsSystem.update(instance, 1 / 60f);
                instance.update(1 / 60f);
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
