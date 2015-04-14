package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public class NewWorldMessage implements Message {

    public enum WorldType {

        Hills
    }

    private WorldType worldType;
    private int seed;
    private boolean teamModeOn;

    public NewWorldMessage() {
    }

    public NewWorldMessage(WorldType worldType, int seed, boolean teamModeOn) {
        this.worldType = worldType;
        this.seed = seed;
        this.teamModeOn = teamModeOn;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public int getSeed() {
        return seed;
    }

    public boolean isTeamModeOn() {
        return teamModeOn;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(worldType.ordinal());
        dos.writeInt(seed);
        dos.writeBoolean(teamModeOn);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        worldType = WorldType.values()[dis.readInt()];
        seed = dis.readInt();
        teamModeOn = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "NewWorldMessage{worldType=" + worldType + ", seed=" + seed + ", teamModeOn=" + teamModeOn + "}";
    }
}
