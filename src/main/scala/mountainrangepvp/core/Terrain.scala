package mountainrangepvp.core

import java.util.{LinkedHashMap, Map}

import com.badlogic.gdx.math.Vector2

/**
 * @author lachlan
 */
object Terrain {
  type Block = Array[Int]

  private val MAX_CACHED_BLOCKS = 100
  private val BLOCK_SIZE = 1024
}

class Terrain(heightMap: HeightMap) {

  import Terrain._

  private val blocks: Map[Integer, Block] = new LinkedHashMap[Integer, Block]() {
    override def removeEldestEntry(eldest: Map.Entry[Integer, Block]) = {
      size > Terrain.MAX_CACHED_BLOCKS
    }
  }

  def seed = heightMap.getSeed

  def sliceAt(base: Int, size: Int) = new Slice(base, size)

  def sliceBetween(start: Int, end: Int) = new Slice(start, end - start)

  def sample(where: Int) = heightMap.getSample(where)

  private def getBlock(blockNumber: Int) = {
    if (blocks.containsKey(blockNumber)) {
      blocks.get(blockNumber)
    }
    else {
      val block = heightMap.getBlock(Terrain.BLOCK_SIZE * blockNumber, Terrain.BLOCK_SIZE)
      blocks.put(blockNumber, block)
      block
    }
  }

  def collideLine(a: Vector2, b: Vector2) = {
    val p1 = if (a.x > b.x) b else a
    val p2 = if (a.x > b.x) a else b

    val width = Math.ceil(p2.x - p1.x).max(1)
    val slice = sliceAt(p1.x.toInt, width.toInt)

    0.until(width.toInt).map(i => i -> slice.get(i)).exists {
      case (i, v) =>
        val lineAtIndex = (width - i) / width * p1.y + i / width * p2.y
        v >= lineAtIndex
    }
  }

  def getHighestPointBetween(start: Int, end: Int) = sliceBetween(start, end).getHighestPoint


  class Slice(base: Int, size: Int) {
    private val offset = base % Terrain.BLOCK_SIZE + (if (base >= 0) 0 else Terrain.BLOCK_SIZE)
    private val blocks = {
      val baseBlock = if (base >= 0)
        base / Terrain.BLOCK_SIZE
      else
        base / Terrain.BLOCK_SIZE - 1

      val neededBlocks = size / Terrain.BLOCK_SIZE + 2
      val array = new Array[Block](neededBlocks)
      0.until(neededBlocks).foreach { i =>
        array(i) = getBlock(baseBlock + i)
      }

      array
    }

    def get(where: Int) = {
      if (where < 0) throw new IllegalArgumentException("Below range of slice: " + where)
      if (where >= size) throw new IllegalArgumentException("Above range of slice: " + where)

      val blockIndex = (where + offset) / Terrain.BLOCK_SIZE
      val block = blocks(blockIndex)
      block((where + offset) % Terrain.BLOCK_SIZE)
    }


    def values = 0.until(size).map(get)

    def valuesWithIndex = 0.until(size).map(i => i -> get(i))


    def getHighestPoint = values.max

    def getLowestPoint = values.min

    def getHighestLeftIndex =
      valuesWithIndex.
      foldLeft((-1, Integer.MAX_VALUE)) {
        case (a@(_, highest), b@(_, value)) => if (value > highest) b else a
      }._1

    def getLeftIndexAbove(above: Int) =
      valuesWithIndex.
      foldLeft((-1, Integer.MAX_VALUE)) {
        case (a@(_, lowest), b@(_, value)) => if (value >= above && value < lowest) b else a
      }._1

    def getRightIndexAbove(above: Int) =
      valuesWithIndex.
      foldRight((-1, Integer.MAX_VALUE)) {
        case (a@(_, lowest), b@(_, value)) => if (value >= above && value < lowest) b else a
      }._1
  }

}
