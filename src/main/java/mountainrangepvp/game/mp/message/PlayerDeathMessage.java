package mountainrangepvp.game.mp.message;

import mountainrangepvp.game.world.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public class PlayerDeathMessage implements Message {

    private int hitID;
    private int hitterID;

    public PlayerDeathMessage() {
    }

    public PlayerDeathMessage(Player hit, Player hitter) {
        this(hit.getID(), hitter.getID());
    }

    public PlayerDeathMessage(int hitID, int hitterID) {
        this.hitID = hitID;
        this.hitterID = hitterID;
    }

    public int getHitID() {
        return hitID;
    }

    public int getHitterID() {
        return hitterID;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(hitID);
        dos.writeInt(hitterID);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        hitID = dis.readInt();
        hitterID = dis.readInt();
    }

    @Override
    public String toString() {
        return "PlayerDeath[" + hitID + ", " + hitterID + "]";
    }
}
