package mountainrangepvp.game.world

import com.badlogic.gdx.math.{Intersector, MathUtils, Rectangle => GdxRect, Vector2}

/**
 * A simulated object in the world.
 */
abstract class Entity {
  val id: Long
  val position: Vector2
  val velocity: Vector2
  val onGround: Boolean
  val bounds: Shape

  def gravity: Float

  val standsOnTerrain: Boolean

  override final def equals(obj: scala.Any): Boolean = obj match {
    case o: Entity => o.id == this.id && o.getClass == this.getClass
    case _ => false
  }
}


sealed trait Shape {
  def sweep(other: Shape): Shape

  def intersects(other: Shape): Boolean

  /** Assumes there is an intersection */
  def intersectionPoint(other: Shape): Vector2
}


case class Point(p: Vector2) extends Shape {
  override def sweep(o: Shape) = o match {
    case Point(op) => Line(p, op)
    case _ => throw new UnsupportedOperationException("Cannot sweep point with " + o)
  }

  override def intersects(o: Shape) = o match {
    case Point(op) => op == p
    case _ => o.intersects(this)
  }

  override def intersectionPoint(o: Shape) = o match {
    case Point(op) => p
    case _ => o.intersectionPoint(this)
  }
}

case class Line(a: Vector2, b: Vector2) extends Shape {
  override def sweep(o: Shape) = o match {
    case Line(oa, ob) => throw new UnsupportedOperationException("Cannot sweep line")
    case _ => throw new UnsupportedOperationException("Cannot sweep line with " + o)
  }

  override def intersects(o: Shape) = o match {
    case Point(p) => Intersector.distanceSegmentPoint(a, b, p) <= MathUtils.FLOAT_ROUNDING_ERROR
    case Line(oa, ob) => Intersector.intersectSegments(a, b, oa, ob, null)
    case _ => o.intersects(this)
  }

  override def intersectionPoint(o: Shape) = o match {
    case Point(p) => p
    case Line(oa, ob) =>
      val tmp = new Vector2
      Intersector.intersectSegments(a, b, oa, ob, tmp)
      tmp

    case _ => o.intersectionPoint(this)
  }
}

case class Rectangle(a: Vector2, b: Vector2) extends Shape {
  val width = b.x - a.x
  val height = b.y - a.y
  val centre = b.cpy().sub(a)
  val topLeft = new Vector2(a.x, b.y)
  val topRight = b
  val bottomLeft = a
  val bottomRight = new Vector2(b.x, a.y)
  private[Rectangle] lazy val gdxRect = new GdxRect(a.x, a.y, width, height)

  override def sweep(o: Shape) = o match {
    case Rectangle(oa, ob) => Rectangle(new Vector2(a.x.min(oa.x), a.y.min(oa.y)),
                                        new Vector2(b.x.min(ob.x), b.y.min(ob.y)))

    case _ => throw new UnsupportedOperationException("Cannot sweep rectangle with " + o)
  }

  override def intersects(o: Shape) = o match {
    case Point(p) => pointIntersect(p)
    case Line(oa, ob) =>
      pointIntersect(oa) || pointIntersect(ob) || lineIntersectsLines(oa, ob)

    case o@Rectangle(oa, ob) => Intersector.intersectRectangles(gdxRect, o.gdxRect, GdxRect.tmp)

    case _ => throw new UnsupportedOperationException("Cannot intersect rectangle with " + o)
  }

  override def intersectionPoint(o: Shape) = o match {
    case Point(p) => p
    case Line(oa, ob) => if (pointIntersect(oa)) oa
    else if (pointIntersect(ob)) ob
    else lineIntersectionPoints(oa, ob).head

    case o@Rectangle(oa, ob) => new Vector2((centre.x + o.centre.x) / 2f,
                                            (centre.y + o.centre.y) / 2f)

    case _ => throw new UnsupportedOperationException("Cannot intersect rectangle with " + o)
  }


  private def pointIntersect(p: Vector2) = (a.x <= p.x && b.x >= p.x) && (a.y <= p.y && b.y >= p.y)

  private def lineIntersectsLines(la: Vector2, lb: Vector2) =
    Intersector.intersectSegments(a.x, a.y, b.x, a.y, la.x, la.y, lb.x, lb.y, null) ||
    Intersector.intersectSegments(a.x, a.y, a.x, b.y, la.x, la.y, lb.x, lb.y, null) ||
    Intersector.intersectSegments(b.x, a.y, b.x, b.y, la.x, la.y, lb.x, lb.y, null) ||
    Intersector.intersectSegments(a.x, b.y, b.x, b.y, la.x, la.y, lb.x, lb.y, null)

  private def lineIntersectionPoints(la: Vector2, lb: Vector2) =
    List(lineIntersectionPoint(bottomLeft, topLeft, la, lb),
         lineIntersectionPoint(bottomLeft, bottomRight, la, lb),
         lineIntersectionPoint(topRight, topLeft, la, lb),
         lineIntersectionPoint(topRight, bottomRight, la, lb)).flatten

  private def lineIntersectionPoint(a1: Vector2, a2: Vector2, b1: Vector2, b2: Vector2) = {
    val tmp = new Vector2
    if (Intersector.intersectSegments(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y, tmp))
      Some(tmp)
    else None
  }
}
