package mountainrangepvp.game;

import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.net.ServerInterface;

/**
 * @author lachlan
 */
public class ClientGame extends Game {

    public ClientGame(GameSettings config, ServerInterface server) {
        super(config, server); // TODO: use either in-process pipe or network pipe
    }
}
