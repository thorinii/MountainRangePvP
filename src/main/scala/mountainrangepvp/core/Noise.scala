package mountainrangepvp.core

class Noise {
  def noise(in: Int): Float = {
    val inAbs = if (in < 0)
      Integer.MAX_VALUE + in
    else
      in

    val inSplat = (inAbs >> 13) ^ inAbs
    val intNoise = (inSplat * (inSplat * inSplat * 60493 + 19990303) + 1376312589) & 0x7fffffff
    val doubleNoise = 1.0 - (intNoise.toDouble / 1073741824.0)
    doubleNoise.toFloat

    /* // a previous algorithm:
    val a = (in >> 13) ^ in
    val s = (in >> 13) ^ (in >> 7) ^ in
    val v = 1.0f - ((s + s * (s + s * 15731 + 789221) + 1376312589) & 0x7ffffff) / 1073741824.0f

    import java.lang.Float
    Float.intBitsToFloat(Float.floatToRawIntBits(v) ^ Float.floatToRawIntBits(Float.intBitsToFloat(a) * v) * s).min(1).max(0)*/
  }
}
