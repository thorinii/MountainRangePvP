package mountainrangepvp.core

abstract class HeightMap {
  def getBlock(base: Int, length: Int) =
    Array.tabulate[Int](length) { i =>
      getSample(base + i)
    }

  def getSample(x: Int): Int

  def getSeed: Int
}
