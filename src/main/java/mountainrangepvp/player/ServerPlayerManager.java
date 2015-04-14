package mountainrangepvp.player;

import mountainrangepvp.Log;
import mountainrangepvp.mp.message.IntroduceMessage;
import mountainrangepvp.mp.message.KillConnectionMessage;
import mountainrangepvp.mp.message.Message;
import mountainrangepvp.mp.message.PlayerUpdateMessage;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author lachlan
 */
public class ServerPlayerManager extends AbstractPlayerManager {

    @Override
    public Player getLocalPlayer() {
        return null;
    }

    @Override
    public void accept(Message message, int id) throws IOException {
        if (message instanceof KillConnectionMessage) {
            Iterator<Player> itr = players.iterator();

            while (itr.hasNext()) {
                Player p = itr.next();
                if (p.getID() == id) {
                    itr.remove();

                    Log.info(p.getName(), "disconnected");
                }
            }

        } else if (message instanceof IntroduceMessage) {
            IntroduceMessage introduceMessage = (IntroduceMessage) message;
            players.add(new Player(introduceMessage.getName(), id,
                                   introduceMessage.getTeam()));

            Log.info(introduceMessage.getName(), "connected");

        } else if (message instanceof PlayerUpdateMessage) {
            PlayerUpdateMessage pum = (PlayerUpdateMessage) message;
            Player p = getPlayer(pum.getPlayer());

            p.getPosition().set(pum.getPos());
            p.getVelocity().set(pum.getVel());
            p.getGunDirection().set(pum.getGun());
        }
    }
}
