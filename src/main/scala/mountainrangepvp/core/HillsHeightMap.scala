package mountainrangepvp.core

import java.lang.Float._

import com.badlogic.gdx.math.MathUtils

/**
 * @author lachlan
 */
object HillsHeightMap {
  private val SCALE = 5
  private val WALL_WIDTH = 50
  private val WALL_DISTANCE = 3000
  private val WALL_HEIGHT = 110
}

class HillsHeightMap(seed: Int) extends HeightMap {
  private val noise = new Noise
  private val makeWalls = false
  private val origin = false

  /**
   * Period, Amplitude
   */
  private val octaves: List[(Float, Float)] = List(
    (100f, 1000f),
    (20f, 70f)
  )

  def getSeed = seed

  def getSample(x: Int) = {
    if (origin && x == 0) {
      -10000
    } else if (makeWalls && Math.abs(x % HillsHeightMap.WALL_DISTANCE) < HillsHeightMap.WALL_WIDTH) {
      val sample = sampleAt(x / HillsHeightMap.WALL_WIDTH * HillsHeightMap.WALL_WIDTH).toInt
      sample + HillsHeightMap.WALL_HEIGHT
    } else
      (0.1f * sampleAt(x) +
       0.9f * (sampleAt(x - 5) + sampleAt(x + 5) +
               sampleAt(x - 10) + sampleAt(x + 10) +
               sampleAt(x - 20) + sampleAt(x + 20)) / 6).
      toInt
  }

  private def sampleAt(at: Float) = {
    val x = at / HillsHeightMap.SCALE
    octaves.map { case (period, amplitude) => interpolatedNoiseAt(x / period) * amplitude }.sum
  }

  private def interpolatedNoiseAt(x: Float) = {
    val integer_X = x.toInt
    val fractional_X = if (x >= 0) x - integer_X else 1 + x - integer_X
    if (fractional_X < 0)
      println(x + " " + fractional_X)
    val v1 = smoothedNoiseAt(integer_X)
    val v2 = smoothedNoiseAt(integer_X + 1)
    (MathUtils.lerp(v1, v2, fractional_X) - 0.9f) * x.abs.min(1f)
  }

  private def smoothedNoiseAt(x: Float) =
    noiseAt(x) / 2 + noiseAt(x - 3) / 4 + noiseAt(x + 3) / 4


  private def noiseAt(x: Float) = noise.noise(floatToIntBits(x) ^ seed)
}
