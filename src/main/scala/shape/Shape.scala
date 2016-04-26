package shape

object Shape {
  case class Point(x: Int, y: Int)

  sealed trait Shape
  case class Circle(radius: Int) extends Shape
  case class Rectangle(width: Int, height: Int, smoothing: Int = 0) extends Shape
  case class Ellipse(width: Int, height: Int) extends Shape

  val draw = (shape: Shape, point: Point) => shape match {
    case Circle(radius) =>
      drawCircle(point, radius)
    case Rectangle(width, height, 0) =>
      drawRectangle(point, width, height)
    case Rectangle(width, height, 100) =>
      drawEllipse(point, width, height)
    case Rectangle(width, height, smoothing) =>
      drawRoundedRectangle(point, width, height, smoothing)
    case Ellipse(width, height) =>
      drawEllipse(point, width, height)
  }

  val drawCircle = (point: Point, radius: Int) =>
    s"Drawing circle at (${point.x}, ${point.y}) Radius: $radius"

  val drawRectangle = (point: Point, width: Int, height: Int) =>
    s"Drawing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"

  val drawEllipse = (point: Point, width: Int, height: Int) =>
    s"Drawing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"

  val drawRoundedRectangle = (point: Point, width: Int, height: Int, smoothing: Int) =>
    s"Drawing rounded rectangle at (${point.x}, ${point.y}) " +
      s"Width $width, Height $height, Smoothing $smoothing"

  val sideCount = (shape: Shape) => shape match {
    case Circle(_) | Ellipse(_, _) => 1
    case Rectangle(_, _, smoothing) if smoothing > 0 => 1
    case Rectangle(_, _, _) => 4
  }
}
