package mountainrangepvp.game.world;

/**
 * @author lachlan
 */
public interface ShotListener {

    public void shotAdd(ShotEntity shot);

    public void shotTerrainCollision(ShotEntity shot);

    public void shotPlayerCollision(ShotEntity shot, Old_Player player);
}
