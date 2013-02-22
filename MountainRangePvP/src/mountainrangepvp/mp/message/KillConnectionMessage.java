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
public class KillConnectionMessage implements Message {

    public enum Reason {

        InvalidHello("Client/server version mismatch"),
        NetworkError("Network transfer error"),
        ClientExit("Client exited"),
        ServerShutdown("Server shutdown");
        final String human;

        private Reason(String human) {
            this.human = human;
        }

        @Override
        public String toString() {
            return human;
        }
    }
    private Reason reason;

    public KillConnectionMessage() {
    }

    public KillConnectionMessage(Reason reason) {
        this.reason = reason;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(reason.ordinal());
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        reason = Reason.values()[dis.readInt()];
    }

    public Reason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "KillConnectionMessage{reason=" + reason + '}';
    }
}
