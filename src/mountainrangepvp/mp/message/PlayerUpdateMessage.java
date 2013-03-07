package mountainrangepvp.mp.message;

import com.badlogic.gdx.math.Vector2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class PlayerUpdateMessage implements Message {

    private int id;
    private Vector2 pos;
    private Vector2 vel;
    private Vector2 gun;
    private boolean alive;
    private int hits;

    public PlayerUpdateMessage() {
    }

    public PlayerUpdateMessage(Player p) {
        this(p.getID(), p.getPosition(), p.getVelocity(), p.getGunDirection(),
             p.isAlive(), p.getHits());
    }

    public PlayerUpdateMessage(int id, Vector2 pos, Vector2 vel, Vector2 gun,
            boolean alive, int hits) {
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.gun = gun;
        this.alive = alive;
        this.hits = hits;
    }

    public int getPlayer() {
        return id;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Vector2 getVel() {
        return vel;
    }

    public Vector2 getGun() {
        return gun;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getHits() {
        return hits;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeInt(id);

        dos.writeFloat(pos.x);
        dos.writeFloat(pos.y);

        dos.writeFloat(vel.x);
        dos.writeFloat(vel.y);

        dos.writeFloat(gun.x);
        dos.writeFloat(gun.y);

        dos.writeBoolean(alive);

        dos.writeInt(hits);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        id = dis.readInt();

        pos = new Vector2();
        pos.x = dis.readFloat();
        pos.y = dis.readFloat();

        vel = new Vector2();
        vel.x = dis.readFloat();
        vel.y = dis.readFloat();

        gun = new Vector2();
        gun.x = dis.readFloat();
        gun.y = dis.readFloat();

        alive = dis.readBoolean();

        hits = dis.readInt();
    }

    @Override
    public String toString() {
        return "PlayerUpdate[" + id + "]";
    }
}
