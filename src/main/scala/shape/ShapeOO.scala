package shape

object ShapeOO {
  case class Point(x: Int, y: Int)

  sealed trait Device
  case class Display() extends Device
  case class Printer(brand: String = "GENERAL") extends Device

  sealed trait Shape {
    def point: Point
    def draw(device: Device, amount: Int): String = this.draw(device, amount)
    def draw(display: Display, amount: Int): String
    def draw(printer: Printer, amount: Int): String
    def sideCount(): Int
    def removeSmoothing(): Shape
    def reform(clazz: Class[_]): Option[Shape]
  }

  case class Circle(point: Point, radius: Int) extends Shape {
    def draw(display: Display, amount: Int): String = {
      if (amount < 100) {
        s"Minimum amount is 100, you have only $amount"
      } else if (amount >= 500) {
        s"Drawing expensive circle at (${point.x}, ${point.y}) Radius: $radius"
      } else {
        s"Drawing circle at (${point.x}, ${point.y}) Radius: $radius"
      }
    }

    def draw(printer: Printer, amount: Int): String = {
      if (amount < 75) {
        s"Minimum amount is 75, you have only $amount"
      } else {
        s"Printing circle at (${point.x}, ${point.y}) Radius: $radius"
      }
    }

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
    def draw(display: Display, amount: Int): String = {
      if (amount < 100) {
        s"Minimum amount is 100, you have only $amount"
      } else smoothing match {
        case 0 => drawRectangle(display)
        case 100 => Ellipse(point, width, height).draw(display, amount)
        case _ => drawRoundedRectangle(display)
      }
    }

    def draw(printer: Printer, amount: Int): String = {
      if (amount < 75) {
        s"Minimum amount is 75, you have only $amount"
      } else smoothing match {
        case 0 => printRectangle(printer)
        case 100 => Ellipse(point, width, height).draw(printer, amount)
        case _ => printRoundedRectangle(printer)
      }
    }

    private def drawRectangle(display: Display): String =
      s"Drawing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"

    private def drawRoundedRectangle(display: Display): String =
      s"Drawing rounded rectangle at (${point.x}, ${point.y}) " +
        s"Width $width, Height $height, Smoothing $smoothing"

    private def printRectangle(printer: Printer): String =
      s"Printing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"

    private def printRoundedRectangle(printer: Printer): String =
      s"Printing rounded rectangle at (${point.x}, ${point.y}) " +
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
    def draw(display: Display, amount: Int): String =
      if (amount < 100) {
        s"Minimum amount is 100, you have only $amount"
      } else {
        s"Drawing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"
      }

    def draw(printer: Printer, amount: Int): String = {
      if (amount < 75) {
        s"Minimum amount is 75, you have only $amount"
      } else {
        s"Printing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"
      }
    }

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
