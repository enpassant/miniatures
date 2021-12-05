package adventofcode

import scala.io.Source

object Puzzles2021 extends App {
  def p2021_5(
    input: String,
    vectorToPixels: PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]]
  ) = {
    Source.fromFile(input)
      .getLines
      .toSeq
      .map(parseLine)
      .collect(vectorToPixels)
      .flatten
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

  def vectorToPixelsHorizontal:
    PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]] =
  {
    case (x1, y1, x2, y2) if (x1 == x2)  =>
      Seq(x1).zipAll(range(y1, y2), x1, -1)
  }

  def vectorToPixelsVertical:
    PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]] =
  {
    case (x1, y1, x2, y2) if (y1 == y2) =>
      range(x1, x2).zipAll(Seq(y1), -1, y1)
  }

  def vectorToPixelsDiagonal:
    PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]] =
  {
    case (x1, y1, x2, y2) =>
      range(x1, x2).zip(range(y1, y2))
  }

  println(
    p2021_5(
      "input_5.txt",
      vectorToPixelsHorizontal orElse
      vectorToPixelsVertical
    ) + ", " +
    p2021_5(
      "input_5.txt",
      vectorToPixelsHorizontal orElse
      vectorToPixelsVertical orElse
      vectorToPixelsDiagonal
    )
  )
}
