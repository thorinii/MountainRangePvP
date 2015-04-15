package mountainrangepvp.mp.message;

import com.badlogic.gdx.math.Vector2;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.Shot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lachlan
 */
public class NewShotMessage implements Message {

    private String player;
    private Vector2 base;
    private Vector2 direction;

    public NewShotMessage() {
    }

    public NewShotMessage(Shot shot) {
        this(shot.player.getName(), shot.base, shot.direction);
    }

    public NewShotMessage(String player, Vector2 base, Vector2 direction) {
        this.player = player;
        this.base = base;
        this.direction = direction;
    }

    @Override
    public void writeOut(DataOutputStream dos) throws IOException {
        dos.writeUTF(player);
        dos.writeFloat(base.x);
        dos.writeFloat(base.y);
        dos.writeFloat(direction.x);
        dos.writeFloat(direction.y);
    }

    @Override
    public void readIn(DataInputStream dis) throws IOException {
        player = dis.readUTF();

        base = new Vector2(dis.readFloat(), dis.readFloat());
        direction = new Vector2(dis.readFloat(), dis.readFloat());
    }

    public Shot getShot(PlayerManager playerManager) {
        return new Shot(base, direction, playerManager.getPlayer(player));
    }

    @Override
    public String toString() {
        return "NewShot[" + player + "]";
    }
}
