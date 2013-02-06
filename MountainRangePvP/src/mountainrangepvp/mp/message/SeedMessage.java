/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.mp.MultiplayerConstants;

/**
 *
 * @author lachlan
 */
public class SeedMessage implements Message {

    private int seed;

    public SeedMessage() {
    }

    public SeedMessage(int seed) {
        this.seed = seed;
    }

    @Override
    public int getCode() {
        return MultiplayerConstants.MESSAGE_SEED;
    }

    public int getSeed() {
        return seed;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(seed);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        seed = dis.readInt();
    }
}
