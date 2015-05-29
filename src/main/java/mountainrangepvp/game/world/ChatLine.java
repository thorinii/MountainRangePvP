package mountainrangepvp.game.world;

/**
 * @author lachlan
 */
public class ChatLine {

    public static final long OLD_TIME = 8000;
    private final Old_Player player;
    private final String playerName;
    private final String text;
    private final long time;

    public ChatLine(Old_Player player, String text) {
        this.player = player;
        this.playerName = (player == null) ? "" : player.getName();
        this.text = text;

        time = System.currentTimeMillis();
    }

    public Old_Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getText() {
        return text;
    }

    public boolean isOld() {
        return System.currentTimeMillis() - time > OLD_TIME;
    }

    @Override
    public String toString() {
        return playerName + ": " + text;
    }
}
