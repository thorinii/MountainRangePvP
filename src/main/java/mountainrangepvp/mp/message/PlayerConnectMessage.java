package mountainrangepvp.mp.message;

import mountainrangepvp.world.player.Player;
import mountainrangepvp.world.player.Player.Team;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public class PlayerConnectMessage implements Message {

    private String playerName;
    private int id;
    private int teamID;

    public PlayerConnectMessage() {
    }

    public PlayerConnectMessage(Player player) {
        this(player.getName(), player.getID(), player.getTeam().ordinal());
    }

    public PlayerConnectMessage(String playerName, int id, int teamID) {
        this.playerName = playerName;
        this.id = id;
        this.teamID = teamID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getID() {
        return id;
    }

    public Team getTeam() {
        return Team.values()[teamID];
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(playerName);
        dos.writeInt(id);
        dos.writeInt(teamID);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        playerName = dis.readUTF();
        id = dis.readInt();
        teamID = dis.readInt();
    }

    @Override
    public String toString() {
        return "PlayerConnect[" + playerName + "; id=" + id + "]";
    }
}
