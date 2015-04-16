package mountainrangepvp.engine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A pub/sub message passing system.
 */
public class EventBus {
    private final Map<Class<? extends Event>, Dispatcher> dispatchers;

    private final AtomicInteger messagesPerFrame;

    public EventBus() {
        dispatchers = new HashMap<>();
        messagesPerFrame = new AtomicInteger(0);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> klass, EventHandler<T> handler) {
        Dispatcher<T> d = (Dispatcher<T>) dispatchers.get(klass);
        if (d == null) {
            d = new Dispatcher<T>();
            dispatchers.put(klass, d);
        }

        d.add(handler);
    }

    public void send(Event event) {
        Dispatcher<?> d = dispatchers.get(event.getClass());
        if (d != null)
            d.dispatch(event);

        messagesPerFrame.incrementAndGet();
    }

    public void flush() {
        messagesPerFrame.set(0);
    }

    public int getMessagesPerFrame() {
        return messagesPerFrame.get();
    }

    private static class Dispatcher<T extends Event> {
        private final List<EventHandler<T>> handlers = new ArrayList<>();

        public void add(EventHandler<T> handler) {
            handlers.add(handler);
        }

        @SuppressWarnings("unchecked")
        public void dispatch(Event e) {
            dispatch0((T) e);
        }

        private void dispatch0(T e) {
            for (EventHandler<T> h : handlers) {
                h.receive((T) e);
            }
        }
    }
}
