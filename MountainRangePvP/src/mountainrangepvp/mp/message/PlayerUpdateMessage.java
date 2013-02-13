/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import com.badlogic.gdx.math.Vector2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mountainrangepvp.mp.MultiplayerConstants;
import mountainrangepvp.player.Player;

/**
 *
 * @author lachlan
 */
public class PlayerUpdateMessage implements Message {

    private String player;
    private Vector2 pos;
    private Vector2 vel;
    private Vector2 gun;
    private boolean alive;

    public PlayerUpdateMessage() {
    }

    public PlayerUpdateMessage(Player p) {
        this(p.getName(), p.getPosition(), p.getVelocity(), p.getGunDirection(),
             p.isAlive());
    }

    public PlayerUpdateMessage(String player, Vector2 pos, Vector2 vel,
            Vector2 gun, boolean alive) {
        this.player = player;
        this.pos = pos;
        this.vel = vel;
        this.gun = gun;
        this.alive = alive;
    }

    public String getPlayer() {
        return player;
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

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(player);

        dos.writeFloat(pos.x);
        dos.writeFloat(pos.y);

        dos.writeFloat(vel.x);
        dos.writeFloat(vel.y);

        dos.writeFloat(gun.x);
        dos.writeFloat(gun.y);

        dos.writeBoolean(alive);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        player = dis.readUTF();

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
    }

    @Override
    public String toString() {
        return "PlayerUpdate[" + player + "]";
    }
}
