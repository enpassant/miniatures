package shape

object ShapePF {
  case class Point(x: Int, y: Int)

  sealed trait Device
  case class Display() extends Device
  case class Printer(brand: String = "GENERAL") extends Device

  sealed trait Shape { def point: Point }
  case class Circle(point: Point, radius: Int) extends Shape
  case class Rectangle(point: Point, width: Int, height: Int, smoothing: Int = 0)
    extends Shape
  case class Ellipse(point: Point, width: Int, height: Int) extends Shape

  type DrawShape = PartialFunction[Shape, String]
  type ExportShape = PartialFunction[(Device, Shape), String]
  type ExportShapeAmount = PartialFunction[(Device, Shape, Int), String]
  type ReformShape = PartialFunction[Shape, Shape]

  val drawCircle: DrawShape = {
    case Circle(point, radius) =>
      s"Drawing circle at (${point.x}, ${point.y}) Radius: $radius"
  }

  val drawRectangle: DrawShape = {
    case Rectangle(point, width, height, _) =>
      s"Drawing rectangle at (${point.x}, ${point.y}) Width $width, Height $height"
  }

  val drawEllipse: DrawShape = {
    case Ellipse(point, width, height) =>
      s"Drawing ellipse at (${point.x}, ${point.y}) Width $width, Height $height"
  }

  val draw = drawCircle orElse drawEllipse orElse drawRectangle

  val drawRectangleAsEllipse: DrawShape = {
    case Rectangle(point, width, height, smoothing) if smoothing == 100 =>
      drawEllipse(Ellipse(point, width, height))
  }

  val drawRoundedRectangle: DrawShape = {
    case Rectangle(point, width, height, smoothing) if smoothing > 0 =>
      s"Drawing rounded rectangle at (${point.x}, ${point.y}) " +
        s"Width $width, Height $height, Smoothing $smoothing"
  }

  val drawWithRoundRectangle = drawCircle orElse drawEllipse orElse
    drawRectangleAsEllipse orElse drawRoundedRectangle orElse drawRectangle

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

  val print = printCircle orElse printEllipse orElse
    printRoundedRectangle orElse printRectangle

  val withDisplay: DrawShape => ExportShape = exportShape => {
    case (Display(), s) => exportShape(s)
  }

  val drawDisplay = withDisplay(drawWithRoundRectangle)

  val export = drawDisplay orElse print

  val printEpsonCircle: ExportShapeAmount = {
    case (Printer("Epson"), Circle(point, radius), _) =>
      s"[Epson] Printing circle at (${point.x}, ${point.y}) Radius: $radius"
  }

  val drawExpensiveCircle: ExportShapeAmount = {
    case (Display(), Circle(point, radius), amount) if amount >= 500 =>
      s"Drawing an expensive circle at (${point.x}, ${point.y}) Radius: $radius"
  }

  val minAmount: Int => (Device, Shape, Int) => Boolean = (minimum) => {
    case (_, _, amount: Int) => amount >= minimum
  }

  val withAmount: ExportShape => ExportShapeAmount = exportShape => {
    case (d, s, _) => exportShape(d, s)
  }

  val drawDisplayAmount = withAmount(drawDisplay)
  val printAmount = withAmount(print)

  val compose: (ExportShapeAmount, (Device, Shape, Int) => Boolean) => ExportShapeAmount =
    (export, filter) =>
  {
    case (device, shape, amount) if export.isDefinedAt(device, shape, amount)
      && filter(device, shape, amount) => export(device, shape, amount)
  }

  val exportMinAmount = compose(
    drawDisplayAmount orElse printEpsonCircle orElse printAmount,
    minAmount(10)
  )

  val exportAmount = compose(drawDisplayAmount, minAmount(100)) orElse
    compose(printEpsonCircle, minAmount(75)) orElse
    compose(printAmount, minAmount(50))

  val exportHighAmount = compose(drawDisplayAmount, minAmount(1000)) orElse
    compose(printEpsonCircle, minAmount(500)) orElse
    compose(printAmount, minAmount(750))

  val reform: ReformShape = {
    case Ellipse(point, width, height) if width == height =>
      Circle(point, width / 2)
    case Rectangle(point, width, height, 100) =>
      Ellipse(point, width, height)
  }

  val applyReform: (ExportShapeAmount, ReformShape) => ExportShapeAmount = (export, reform) => {
    case (device, shape, amount) if export.isDefinedAt(device, shape, amount) =>
      if (reform.isDefinedAt(shape)) {
        val reformedShape = reform(shape)
        if (export.isDefinedAt(device, reformedShape, amount)) {
          applyReform(export, reform)(device, reformedShape, amount)
        } else {
          export(device, shape, amount)
        }
      } else {
        export(device, shape, amount)
      }
  }

  val exportUnknown: ExportShapeAmount = {
    case (device, shape, _) => s"Unknown device $device or shape $shape"
  }

  val draw1 = withAmount(drawDisplay)
  val draw2 = withAmount(withDisplay(drawRoundedRectangle orElse drawRectangle))
  val draw3 = withAmount(withDisplay(drawEllipse orElse drawRoundedRectangle orElse drawRectangle))

  val drawWithReform = applyReform(withAmount(drawDisplay), reform) orElse exportUnknown
  val drawWithReform1 = applyReform(draw1, reform) orElse exportUnknown
  val drawWithReform2 = applyReform(draw2, reform) orElse exportUnknown
  val drawWithReform3 = applyReform(draw3, reform) orElse exportUnknown

  val print1 = withAmount(printRectangle)
  val print2 = withAmount(printRoundedRectangle orElse printRectangle)
  val print3 = withAmount(printEllipse orElse printRoundedRectangle orElse printRectangle)

  val printWithReform = applyReform(withAmount(print), reform) orElse exportUnknown
  val printWithReform1 = applyReform(print1, reform) orElse exportUnknown
  val printWithReform2 = applyReform(print2, reform) orElse exportUnknown
  val printWithReform3 = applyReform(print3, reform) orElse exportUnknown

  val exportWithReform = applyReform(exportAmount, reform) orElse exportUnknown

  val sideCount = (shape: Shape) => shape match {
    case Circle(_, _) | Ellipse(_, _, _) => 1
    case Rectangle(_, _, _, smoothing) if smoothing > 0 => 1
    case Rectangle(_, _, _, _) => 4
  }
}
