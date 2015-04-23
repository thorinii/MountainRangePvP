package mountainrangepvp.engine.util;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A pub/sub message passing system.
 */
public class EventBus {
    private final Thread dispatchThread;
    private final Map<Class<? extends Event>, Dispatcher> dispatchers;
    private final Queue<Event> pendingMessages;

    private final AtomicInteger messagesPerFrame;

    public EventBus(Thread dispatchThread) {
        this.dispatchThread = dispatchThread;
        dispatchers = new HashMap<>();
        pendingMessages = new LinkedBlockingQueue<>();
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
        if (Thread.currentThread() == dispatchThread)
            dispatch(event);
        else
            pendingMessages.offer(event);
    }

    private void dispatch(Event event) {
        Dispatcher<?> d = dispatchers.get(event.getClass());
        if (d == null)
            Log.fine("No handlers subscribed to " + event);
        else
            d.dispatch(event);

        messagesPerFrame.incrementAndGet();
    }

    /**
     * Assumes running on the dispatch thread.
     */
    public void flushPendingMessages() {
        while (!pendingMessages.isEmpty())
            dispatch(pendingMessages.remove());
    }

    public void resetMessagesPerFrame() {
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
