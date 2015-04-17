package mountainrangepvp.net.client;

import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.game.event.PlayerFiredEvent;
import mountainrangepvp.net.NetworkConstants;
import mountainrangepvp.net.ServerInterface;

/**
 * That which talks to the server, whether by the network or in-process calls.
 */
public class Client {
    private final EventBus eventbus;
    private final ServerInterface server;
    private final String nickname;

    public static Client newClient(EventBus eventbus, ServerInterface server, String nickname) {
        Client c = new Client(eventbus, server, nickname);
        c.subscribe();
        return c;
    }

    private Client(EventBus eventbus, ServerInterface server, String nickname) {
        this.eventbus = eventbus;
        this.server = server;
        this.nickname = nickname;
    }

    private void subscribe() {
        eventbus.subscribe(PlayerFiredEvent.class, new EventHandler<PlayerFiredEvent>() {
            @Override
            public void receive(PlayerFiredEvent e) {
                System.out.println("TODO: " + e);
            }
        });
    }

    public void start() {
        server.connect(NetworkConstants.CHECK_CODE,
                       NetworkConstants.VERSION,
                       nickname);
    }
}
