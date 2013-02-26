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
public class IntroduceMessage implements Message {

    private String name;

    public IntroduceMessage() {
    }

    public IntroduceMessage(String name) {
        this.name = name;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        name = dis.readUTF();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "IntroduceMessage{name=" + name + '}';
    }
}
