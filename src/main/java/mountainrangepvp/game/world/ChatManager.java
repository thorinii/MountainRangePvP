package mountainrangepvp.game.world;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lachlan
 */
public class ChatManager {

    private final List<ChatLine> lines;

    private boolean chatting;
    private String currentLine;

    public ChatManager() {
        lines = new LinkedList<>();
    }

    public List<ChatLine> getLinesHead(int length) {
        return Collections.unmodifiableList(lines.subList(0,
                                                          Math.min(lines.size(),
                                                                   length)));
    }

    public boolean isChatting() {
        return chatting;
    }

    public String getCurrentLine() {
        return currentLine;
    }
}
