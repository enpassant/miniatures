package shape

object ShapeOO {
  case class Point(x: Int, y: Int)

  sealed trait Shape {
    def point: Point
    def draw: String
    def sideCount(): Int
    def removeSmoothing(): Shape
    def reform(clazz: Class[_]): Option[Shape]
  }

  case class Circle(point: Point, radius: Int) extends Shape {
    def draw: String =
      s"Drawing circle at (${point.x}, ${point.y}) Radius: $radius"

    def sideCount() = 1

    def removeSmoothing(): Shape = this

    def reform(clazz: Class[_]): Option[Shape] =
      if (getClass == Circle.getClass)
        Some(this)
      else if (clazz == Ellipse.getClass)
        Some(Ellipse(point, radius*2, radius*2))
      else if (clazz == Rectangle.getClass)
        reform(Ellipse.getClass) flatMap { ellipse => ellipse.reform(Rectangle.getClass) }
      else None
  }

  case class Rectangle(point: Point, width: Int, height: Int, smoothing: Int = 0) extends Shape {
    def draw: String = smoothing match {
      case 0 => drawRectangle
      case 100 => Ellipse(point, width, height).draw
      case _ => drawRoundedRectangle
    }

    private def drawRectangle: String =
      s"Drawing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"

    private def drawRoundedRectangle: String =
      s"Drawing rounded rectangle at (${point.x}, ${point.y}) " +
        s"Width $width, Height $height, Smoothing $smoothing"

    def sideCount() = if (smoothing > 0) 1 else 4

    def removeSmoothing(): Shape = copy(smoothing=0)

    def reform(clazz: Class[_]): Option[Shape] =
      if (getClass == Rectangle.getClass)
        Some(this)
      else if (clazz == Ellipse.getClass && smoothing == 100)
        Some(Ellipse(point, width, height))
      else if (clazz == Circle.getClass)
        reform(Ellipse.getClass) flatMap { ellipse => ellipse.reform(Circle.getClass) }
      else None
  }

  case class Ellipse(point: Point, width: Int, height: Int) extends Shape {
    def draw: String =
      s"Drawing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"

    def sideCount() = 1

    def removeSmoothing(): Shape = this

    def reform(clazz: Class[_]): Option[Shape] =
      if (getClass == Ellipse.getClass)
        Some(this)
      else if (clazz == Rectangle.getClass)
        Some(Rectangle(point, width, height, 100))
      else if (clazz == Circle.getClass && width == height)
        Some(Circle(point, width / 2))
      else None
  }
}
