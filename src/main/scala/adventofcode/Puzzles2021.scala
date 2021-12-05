package adventofcode

import scala.io.Source

object Puzzles2021 extends App {
  def p2021_5(
    input: String,
    filter: (Int, Int, Int, Int) => Boolean
  ) = {
    Source.fromFile(input)
      .getLines
      .toSeq
      .map(parseLine)
      .filter(filter.tupled)
      .flatMap(vectorToPixels.tupled)
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .filter { case (k, v) => v > 1 }
      .size
  }

  val Pattern = "(\\d+),(\\d+) -> (\\d+),(\\d+)".r

  def parseLine(str: String) = str match {
    case Pattern(x1, y1, x2, y2) =>
      (x1.toInt, y1.toInt, x2.toInt, y2.toInt)
  }

  def range(x1: Int, x2: Int) =
      if (x2 > x1) {
        (x1 to x2)
      } else {
        (x1 to x2 by -1)
      }

  def every(x1: Int, y1: Int, x2: Int, y2:Int) = true

  def isNotDiagonal(x1: Int, y1: Int, x2: Int, y2:Int) =
    (x1 == x2) || (y1 == y2)

  val vectorToPixels = (x1: Int, y1: Int, x2: Int, y2:Int) =>
      range(x1, x2).zipAll(range(y1, y2), x1, y1)

  println(
    p2021_5(
      "input_5.txt",
      isNotDiagonal
    ) + ", " +
    p2021_5(
      "input_5.txt",
      every
    )
  )
}
