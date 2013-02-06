/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author lachlan
 */
public class MessageQueue {

    private final Object lock = new Object();
    private final Queue<Message> messages;
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

    public void pushMessage(Message m) {
        synchronized (lock) {
            messages.add(m);
        }
    }

    public void update() {
        List<MessageListener> listeners = new ArrayList<>(this.listeners);
        List<Message> messages;

        synchronized (lock) {
            messages = new ArrayList(this.messages);
            this.messages.clear();
        }

        for (Message m : messages) {
            for (MessageListener listener : listeners) {
                listener.accept(m);
            }
        }
    }
}
