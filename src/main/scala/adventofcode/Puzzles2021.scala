package adventofcode

import scala.io.Source

object Puzzles2021 extends App {
  def p2021_5(
    input: String,
    vectorToPixels: PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]]
  ) = {
    Source.fromFile(input)
      .getLines
      .map(
        line => line.split(" -> ")
          .flatMap ( pairs => pairs.split(","))
          .map(Integer.parseInt(_) )
        )
      .toSeq
      .map(a => (a(0), a(1), a(2), a(3)))
      .collect(vectorToPixels)
      .flatten
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .filter { case (k, v) => v > 1 }
      .size
  }

  def vectorToPixelsHorizontal:
    PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]] =
  {
    case (x1, y1, x2, y2) if (x1 == x2)  => {
      if (y2 > y1) {
        (y1 to y2).map(y => (x1, y))
      } else {
        (y2 to y1).map(y => (x1, y))
      }
    }
  }

  def vectorToPixelsVertical:
    PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]] =
  {
    case (x1, y1, x2, y2) if (y1 == y2) => {
      if (x2 > x1) {
        (x1 to x2).map(x => (x, y1))
      } else {
        (x2 to x1).map(x => (x, y1))
      }
    }
  }

  def vectorToPixelsDiagonal:
    PartialFunction[(Int, Int, Int, Int), Seq[(Int, Int)]] =
  {
    case (x1, y1, x2, y2) => {
      val xRange = if (x2 > x1) {
        (x1 to x2)
      } else {
        (x1 to x2 by -1)
      }
      val yRange = if (y2 > y1) {
        (y1 to y2)
      } else {
        (y1 to y2 by -1)
      }
      xRange.zip(yRange)
    }
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
