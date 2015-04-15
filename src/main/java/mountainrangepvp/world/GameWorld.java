package mountainrangepvp.world;

import mountainrangepvp.world.chat.ChatManager;
import mountainrangepvp.world.player.PlayerManager;
import mountainrangepvp.world.shot.ShotManager;
import mountainrangepvp.world.terrain.Terrain;

/**
 * @author lachlan
 */
public class GameWorld {

    private Terrain terrain;
    private PlayerManager playerManager;
    private ShotManager shotManager;
    private ChatManager chatManager;
    private boolean teamModeOn;

    public GameWorld() {
        teamModeOn = false;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public ShotManager getShotManager() {
        return shotManager;
    }

    public void setShotManager(ShotManager shotManager) {
        this.shotManager = shotManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public boolean isTeamModeOn() {
        return teamModeOn;
    }

    public void setTeamModeOn(boolean teamModeOn) {
        this.teamModeOn = teamModeOn;
    }

    public void update(float dt) {
        shotManager.update(dt);
    }
}
