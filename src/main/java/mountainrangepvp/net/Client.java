package mountainrangepvp.net;

import mountainrangepvp.engine.util.EventBus;
import mountainrangepvp.engine.util.EventHandler;
import mountainrangepvp.game.event.PlayerFiredEvent;

/**
 * That which talks to the server, whether by the network or in-process calls.
 */
public class Client {
    private final EventBus eventbus;
    private final ServerInterface server;

    public static Client newClient(EventBus eventbus, ServerInterface server) {
        Client c = new Client(eventbus, server);
        c.subscribe();
        return c;
    }

    private Client(EventBus eventbus, ServerInterface server) {
        this.eventbus = eventbus;
        this.server = server;
    }

    private void subscribe() {
        eventbus.subscribe(PlayerFiredEvent.class, new EventHandler<PlayerFiredEvent>() {
            @Override
            public void receive(PlayerFiredEvent e) {
                System.out.println("received: " + e);
            }
        });
    }
}
