/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.chat;

import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class ChatLine {

    public static final long OLD_TIME = 8000;
    private final Player player;
    private final String playerName;
    private final String text;
    private final long time;

    public ChatLine(Player player, String text) {
        this.player = player;
        this.playerName = (player == null) ? "" : player.getName();
        this.text = text;

        time = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }

    public boolean isOld() {
        return System.currentTimeMillis() - time > OLD_TIME;
    }

    @Override
    public String toString() {
        return playerName + ": " + text;
    }

    public boolean isServer() {
        return player == null;
    }
}
