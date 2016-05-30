package shape

object ShapePF {
  case class Point(x: Int, y: Int)

  sealed trait Device
  case class Display() extends Device
  case class Printer(brand: String = "GENERAL") extends Device

  sealed trait Shape { def point: Point }
  case class Circle(point: Point, radius: Int) extends Shape
  case class Rectangle(point: Point, width: Int, height: Int, smoothing: Int = 0) extends Shape
  case class Ellipse(point: Point, width: Int, height: Int) extends Shape

  type ExportShape = PartialFunction[(Device, Shape), String]
  type ReformShape = PartialFunction[Shape, Shape]

  val drawCircle: ExportShape = {
    case (Display(), Circle(point, radius)) =>
      s"Drawing circle at (${point.x}, ${point.y}) Radius: $radius"
  }

  val drawRectangle: ExportShape = {
    case (Display(), Rectangle(point, width, height, _)) =>
      s"Drawing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"
  }

  val drawEllipse: ExportShape = {
    case (Display(), Ellipse(point, width, height)) =>
      s"Drawing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"
  }

  val drawRoundedRectangle: ExportShape = {
    case (Display(), Rectangle(point, width, height, smoothing)) if smoothing > 0 =>
      s"Drawing rounded rectangle at (${point.x}, ${point.y}) " +
        s"Width $width, Height $height, Smoothing $smoothing"
  }

  val printCircle: ExportShape = {
    case (Printer(_), Circle(point, radius)) =>
      s"Printing circle at (${point.x}, ${point.y}) Radius: $radius"
  }

  val printRectangle: ExportShape = {
    case (Printer(_), Rectangle(point, width, height, _)) =>
      s"Printing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"
  }

  val printEllipse: ExportShape = {
    case (Printer(_), Ellipse(point, width, height)) =>
      s"Printing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"
  }

  val printRoundedRectangle: ExportShape = {
    case (Printer(_), Rectangle(point, width, height, smoothing)) if smoothing > 0 =>
      s"Printing rounded rectangle at (${point.x}, ${point.y}) " +
        s"Width $width, Height $height, Smoothing $smoothing"
  }

  val printEpsonCircle: ExportShape = {
    case (Printer("Epson"), Circle(point, radius)) =>
      s"[Epson] Printing circle at (${point.x}, ${point.y}) Radius: $radius"
  }

  val reform: ReformShape = {
    case Ellipse(point, width, height) if width == height =>
      Circle(point, width / 2)
    case Rectangle(point, width, height, 100) =>
      Ellipse(point, width, height)
  }

  val applyReform: (ExportShape, ReformShape) => ExportShape = (export, reform) => {
    case (device: Device, shape: Shape) if export.isDefinedAt(device, shape) =>
      if (reform.isDefinedAt(shape)) {
        val reformedShape = reform(shape)
        if (export.isDefinedAt(device, reformedShape)) {
          applyReform(export, reform)(device, reformedShape)
        } else {
          export(device, shape)
        }
      } else {
        export(device, shape)
      }
  }

  val exportUnknown: ExportShape = {
    case (device, shape) => s"Unknown device $device or shape $shape"
  }

  val draw = drawCircle orElse drawEllipse orElse drawRoundedRectangle orElse drawRectangle
  val draw1 = drawRectangle
  val draw2 = drawRoundedRectangle orElse drawRectangle
  val draw3 = drawEllipse orElse drawRoundedRectangle orElse drawRectangle

  val drawWithReform = applyReform(draw, reform) orElse exportUnknown
  val drawWithReform1 = applyReform(draw1, reform) orElse exportUnknown
  val drawWithReform2 = applyReform(draw2, reform) orElse exportUnknown
  val drawWithReform3 = applyReform(draw3, reform) orElse exportUnknown

  val print = printCircle orElse printEllipse orElse printRoundedRectangle orElse printRectangle
  val print1 = printRectangle
  val print2 = printRoundedRectangle orElse printRectangle
  val print3 = printEllipse orElse printRoundedRectangle orElse printRectangle

  val printWithReform = applyReform(print, reform) orElse exportUnknown
  val printWithReform1 = applyReform(print1, reform) orElse exportUnknown
  val printWithReform2 = applyReform(print2, reform) orElse exportUnknown
  val printWithReform3 = applyReform(print3, reform) orElse exportUnknown

  val export = draw orElse printEpsonCircle orElse print

  val exportWithReform = applyReform(export, reform) orElse exportUnknown

  val sideCount = (shape: Shape) => shape match {
    case Circle(_, _) | Ellipse(_, _, _) => 1
    case Rectangle(_, _, _, smoothing) if smoothing > 0 => 1
    case Rectangle(_, _, _, _) => 4
  }
}
