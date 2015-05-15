package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2

/**
 * Holds the point the camera is centred on.
 */
case class Camera(centre: Vector2) {
  def centreOn(player: PlayerEntity) = {
    val nextCentre = centre.cpy()
    nextCentre.lerp(player.position, 0.2f)
    Camera(nextCentre)
  }
}
