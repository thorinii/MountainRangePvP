/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.player;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author lachlan
 */
public class Player {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 60;
    //
    private final String name;
    private final Vector2 position;
    private final Vector2 velocity;

    public Player(String name) {
        this.name = name;

        this.position = new Vector2();
        this.velocity = new Vector2();
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
