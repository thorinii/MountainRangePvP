/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.player.Player;
import mountainrangepvp.player.Player.Team;

/**
 *
 * @author lachlan
 */
public class IntroduceMessage implements Message {

    private String name;
    private int teamID;

    public IntroduceMessage() {
    }

    public IntroduceMessage(String name, Team team) {
        this(name, team.ordinal());
    }

    public IntroduceMessage(String name, int teamID) {
        this.name = name;
        this.teamID = teamID;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
        dos.writeInt(teamID);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        name = dis.readUTF();
        teamID = dis.readInt();
    }

    public String getName() {
        return name;
    }

    public Player.Team getTeam() {
        return Player.Team.values()[teamID];
    }

    @Override
    public String toString() {
        return "IntroduceMessage{name=" + name + '}';
    }
}
