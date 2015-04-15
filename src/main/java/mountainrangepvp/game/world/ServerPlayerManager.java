package mountainrangepvp.game.world;

import mountainrangepvp.game.mp.message.IntroduceMessage;
import mountainrangepvp.game.mp.message.KillConnectionMessage;
import mountainrangepvp.game.mp.message.Message;
import mountainrangepvp.game.mp.message.PlayerUpdateMessage;
import mountainrangepvp.util.Log;

import java.io.IOException;

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
            for (Player p : players) {
                if (p.getID() == id) {
                    players.remove(p);

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
