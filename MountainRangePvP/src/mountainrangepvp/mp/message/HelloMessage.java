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
public class HelloMessage implements Message {

    private boolean valid;

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(MultiplayerConstants.CHECK_CODE);
        dos.writeInt(MultiplayerConstants.VERSION);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        int checkCode = dis.readInt();
        int version = dis.readInt();

        if (checkCode != MultiplayerConstants.CHECK_CODE) {
            valid = false;
        } else if (version != MultiplayerConstants.VERSION) {
            valid = false;
        } else {
            valid = true;
        }
    }

    public boolean isValid() {
        return valid;
    }
}