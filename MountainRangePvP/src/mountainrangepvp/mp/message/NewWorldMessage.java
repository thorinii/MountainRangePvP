/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author lachlan
 */
public class NewWorldMessage implements Message {

    public enum WorldType {

        Hills
    }
    private WorldType worldType;
    private int seed;

    public NewWorldMessage() {
    }

    public NewWorldMessage(WorldType worldType, int seed) {
        this.worldType = worldType;
        this.seed = seed;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public int getSeed() {
        return seed;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(worldType.ordinal());
        dos.writeInt(seed);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        worldType = WorldType.values()[dis.readInt()];
        seed = dis.readInt();
    }

    @Override
    public String toString() {
        return "NewWorldMessage{worldType=" + worldType + ", seed=" + seed + '}';
    }
}
