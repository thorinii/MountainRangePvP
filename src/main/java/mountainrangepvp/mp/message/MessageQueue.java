package mountainrangepvp.mp.message;

import mountainrangepvp.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lachlan
 */
public class MessageQueue {

    private final Queue<MessageHolder> messages;
    private final List<MessageListener> listeners;
    private boolean inQueueMode;

    public MessageQueue() {
        messages = new LinkedBlockingQueue<>();
        listeners = new ArrayList<>();

        inQueueMode = true;
    }

    public void setInQueueMode(boolean inQueueMode) {
        this.inQueueMode = inQueueMode;
    }

    public boolean isInQueueMode() {
        return inQueueMode;
    }

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    public void pushMessage(Message m) {
        pushMessage(m, 0);
    }

    public void pushMessage(Message m, int id) {
        if (inQueueMode) {
            messages.add(new MessageHolder(m, id));
        } else {
            try {
                for (MessageListener listener : listeners) {
                    listener.accept(m, id);
                }
            } catch (IOException ioe) {
                Log.warn("Error while direct-processing messages", ioe);
            }
        }
    }

    public void update() throws IOException {
        List<MessageListener> listeners = new ArrayList<>(this.listeners);

        while (!messages.isEmpty()) {
            MessageHolder m = messages.poll();
            for (MessageListener listener : listeners) {
                listener.accept(m.message, m.id);
            }
        }
    }

    private static class MessageHolder {

        final Message message;
        final int id;

        public MessageHolder(Message message, int id) {
            this.message = message;
            this.id = id;
        }
    }
}
