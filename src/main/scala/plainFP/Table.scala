package plainFP

object TableAPI {
  case class Table(rows: Vector[Row])
  case class Row(cells: Vector[Cell])
  case class Cell(value: String)

  val table = (init: Table => Table) => {
    val t = Table(Vector())
    init(t)
  }

  val row = (init: Row => Row) => (t: Table) => {
    val r = Row(Vector())
    Table(t.rows :+ init(r))
  }

  val cell = (str: String) => (r: Row) => {
    Row(r.cells :+ Cell(str))
  }
}

object TableApp extends App {
  import TableAPI._

  val t = table {
    row {
      cell("top left") andThen
      cell("top right")
    } andThen
    row {
      cell("bottom left") andThen
      cell("bottom right")
    }
  }

  println(t)
}
