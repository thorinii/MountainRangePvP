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
public interface Message {

    public void writeOut(DataOutputStream dos) throws IOException;

    public void readIn(DataInputStream dis) throws IOException;
}
