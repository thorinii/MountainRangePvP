/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import mountainrangepvp.mp.Proxy;

/**
 *
 * @author lachlan
 */
public class MessageQueue {

    private final Object lock = new Object();
    private final Queue<MessageHolder> messages;
    private final List<MessageListener> listeners;

    public MessageQueue() {
        messages = new LinkedBlockingQueue<>();
        listeners = new ArrayList<>();
    }

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    public void pushMessage(Message m, Proxy proxy) {
        synchronized (lock) {
            messages.add(new MessageHolder(m, proxy));
        }
    }

    public void update() throws IOException {
        List<MessageListener> listeners = new ArrayList<>(this.listeners);
        List<MessageHolder> messages;

        synchronized (lock) {
            messages = new ArrayList(this.messages);
            this.messages.clear();
        }

        for (MessageHolder m : messages) {
            for (MessageListener listener : listeners) {
                listener.accept(m.message, m.proxy);
            }
        }
    }

    private static class MessageHolder {

        final Message message;
        final Proxy proxy;

        public MessageHolder(Message message, Proxy proxy) {
            this.message = message;
            this.proxy = proxy;
        }
    }
}
