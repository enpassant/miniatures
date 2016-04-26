package shape

object ShapeSimple {
  case class Point(x: Int, y: Int)

  case class Shape(form: Symbol, radius: Int = 0,
    width: Int = 0, height: Int = 0,
    smoothing: Int = 0)

  val createCircle = (radius: Int) =>
    Shape('Circle, radius=radius)
  val createRectangle = (width: Int, height: Int) =>
    Shape('Rectangle, width=width, height=height)
  val createEllipse = (width: Int, height: Int) =>
    Shape('Ellipse, width=width, height=height)
  val createRoundedRectangle = (width: Int, height: Int, smoothing: Int) =>
    Shape('Rectangle, width=width, height=height, smoothing=smoothing)

  val draw = (point: Point, shape: Shape) => shape match {
    case Shape('Circle, radius, _, _, _) =>
      drawCircle(point, radius)
    case Shape('Rectangle, _, width, height, 0) =>
      drawRectangle(point, width, height)
    case Shape('Rectangle, _, _, _, 100) | Shape('Ellipse, _, _, _, _) =>
      drawEllipse(point, shape.width, shape.height)
    case Shape('Rectangle, _, width, height, smoothing) =>
      drawRoundedRectangle(point, width, height, smoothing)
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
    case Shape('Circle, _, _, _, _) | Shape('Ellipse, _, _, _, _) => 1
    case Shape('Rectangle, _, _, _, smoothing) if smoothing > 0 => 1
    case Shape('Rectangle, _, _, _, _) => 4
  }

  val removeSmoothing = (shape: Shape) => shape.copy(smoothing=0)

  val reform: (Shape, Symbol) => Option[Shape] = (shape, to) => (shape.form, to) match {
      case (a, b) if a == b =>
        Some(shape)
      case ('Circle, 'Ellipse) =>
        Some(Shape('Ellipse, width=shape.radius*2, height=shape.radius*2))
      case ('Ellipse, 'Rectangle) =>
        Some(shape.copy(form='Rectangle, smoothing=100))
      case ('Circle, 'Rectangle) =>
        reform(shape, 'Ellipse) flatMap { ellipse => reform(ellipse, 'Rectangle) }
      case ('Ellipse, 'Circle) if shape.width == shape.height =>
        Some(Shape('Circle, radius= shape.width / 2))
      case ('Rectangle, 'Ellipse) if shape.smoothing == 100 =>
        Some(shape.copy(form='Ellipse, smoothing=0))
      case ('Rectangle, 'Circle) =>
        reform(shape, 'Ellipse) flatMap { ellipse => reform(ellipse, 'Circle) }
      case _ =>
        None
  }
}
