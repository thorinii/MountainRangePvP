package mountainrangepvp.game.world

import com.badlogic.gdx.math.{MathUtils, Vector2}

/**
 * Holds the point the camera is centred on.
 */
case class Camera(centre: Vector2) {
  val RunningLookAhead = 250
  val AimingLookAhead = 100
  val VerticalShift = +70

  def centreOn(player: PlayerEntity) = {
    val target = player.position.cpy()
    target.add(0, VerticalShift)

    val lookAhead = MathUtils.lerp(AimingLookAhead, RunningLookAhead,
                                   (player.velocity.x / 400).abs.max(0).min(1))
    target.add(player.aim.cpy().scl(lookAhead))


    val nextCentre = centre.cpy()
    nextCentre.lerp(target, 0.15f)
    Camera(nextCentre)
  }
}
