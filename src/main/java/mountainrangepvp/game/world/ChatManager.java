package mountainrangepvp.game.world;

import mountainrangepvp.game.mp.message.Message;
import mountainrangepvp.game.mp.message.MessageListener;
import mountainrangepvp.game.mp.message.NewChatMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lachlan
 */
public class ChatManager implements MessageListener {

    private final PlayerManager playerManager;
    private final List<ChatLine> lines;
    private final List<ChatListener> listeners;

    private boolean chatting;
    private String currentLine;

    public ChatManager(PlayerManager playerManager) {
        this.playerManager = playerManager;

        lines = new LinkedList<>();
        listeners = new ArrayList<>();
    }

    public List<ChatLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public List<ChatLine> getLinesHead(int length) {
        return Collections.unmodifiableList(lines.subList(0,
                                                          Math.min(lines.size(),
                                                                   length)));
    }

    /**
     * Something spoken by the server.
     */
    public void addLine(String text) {
        addLine(new ChatLine(null, text));
    }

    public void addLine(Player player, String text) {
        addLine(new ChatLine(player, text));
    }

    public void addLocalLine(String text) {
        addLine(playerManager.getLocalPlayer(), text);
    }

    public void addLine(ChatLine line) {
        lines.add(0, line);

        for (ChatListener listener : listeners) {
            listener.onMessage(line);
        }
    }

    public void addChatListener(ChatListener listener) {
        listeners.add(listener);
    }

    public void removeChatListener(ChatListener listener) {
        listeners.remove(listener);
    }

    public boolean isChatting() {
        return chatting;
    }

    public void setChatting(boolean chatting) {
        this.chatting = chatting;
        if (chatting)
            currentLine = "";
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }

    @Override
    public void accept(Message message, int id) throws IOException {
        if (message instanceof NewChatMessage) {
            NewChatMessage ncm = (NewChatMessage) message;

            addLine(ncm.getLine(playerManager));
        }
    }
}
