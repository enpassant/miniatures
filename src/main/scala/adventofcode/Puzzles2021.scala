package adventofcode

import scala.io.Source

object Puzzles2021 extends App {
  val day = 7;

  def p2021_5(
    input: String,
    filter: (Int, Int, Int, Int) => Boolean
  ) = {
    Source.fromFile(input)
      .getLines()
      .toSeq
      .map(parseLine)
      .filter(filter.tupled)
      .flatMap(vectorToPixels.tupled)
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .filter { case (k, v) => v > 1 }
      .size
  }

  if (day == 5) {
    val round1 = p2021_5(
        "input_5.txt",
        isNotDiagonal
      )
    val round2 = p2021_5(
        "input_5.txt",
        every
      )
    println(s"$round1, $round2")
  }

  def p2021_6_1(
    input: String
  ) = {
    val population = input
      .split(",")
      .zipWithIndex
      .map(v => v._2 -> v._1.toInt)
      .toMap

    (1 to 80)
      .foldLeft(population) {
        (popu, i) =>
          popu.foldLeft(popu) {
            case (p, (i, day)) => if (day == 0) {
              (p + (p.size -> 8)).updated(i, 6)
            } else {
              p.updated(i, day -1)
            }
          }
      }.size
  }

  def p2021_6_2(
    input: String
  ) = {
    val population = input
      .split(",")
      .map(_.toInt)
      .foldLeft(Vector(0, 0, 0, 0, 0, 0, 0, 0, 0)) {
        case (vec, day) => vec.updated(day, vec(day) + 1)
      }
      .map(BigInt(_))

    (1 to 256)
      .foldRight(population) {
        (i, popu) =>
          (popu.drop(1) :+ popu(0)).updated(6, popu(7) + popu(0))
      }.sum
  }

  if (day == 6) {
    val round1 = p2021_6_2(
        "3,4,3,1,2"
      )
    val round2 = p2021_6_2(
        "5,1,2,1,5,3,1,1,1,1,1,2,5,4,1,1,1,1,2,1,2,1,1,1,1,1,2,1,5,1,1,1,3,1,1,1,3,1,1,3,1,1,4,3,1,1,4,1,1,1,1,2,1,1,1,5,1,1,5,1,1,1,4,4,2,5,1,1,5,1,1,2,2,1,2,1,1,5,3,1,2,1,1,3,1,4,3,3,1,1,3,1,5,1,1,3,1,1,4,4,1,1,1,5,1,1,1,4,4,1,3,1,4,1,1,4,5,1,1,1,4,3,1,4,1,1,4,4,3,5,1,2,2,1,2,2,1,1,1,2,1,1,1,4,1,1,3,1,1,2,1,4,1,1,1,1,1,1,1,1,2,2,1,1,5,5,1,1,1,5,1,1,1,1,5,1,3,2,1,1,5,2,3,1,2,2,2,5,1,1,3,1,1,1,5,1,4,1,1,1,3,2,1,3,3,1,3,1,1,1,1,1,1,1,2,3,1,5,1,4,1,3,5,1,1,1,2,2,1,1,1,1,5,4,1,1,3,1,2,4,2,1,1,3,5,1,1,1,3,1,1,1,5,1,1,1,1,1,3,1,1,1,4,1,1,1,1,2,2,1,1,1,1,5,3,1,2,3,4,1,1,5,1,2,4,2,1,1,1,2,1,1,1,1,1,1,1,4,1,5"
      )
    println(s"$round1, $round2")
  }

  def p2021_7(
    input: String,
    calcFn: (Seq[Int], Int) => Int
  ) = {
    val population = Source.fromFile(input)
        .mkString
        .split(",")
        .map(_.trim.toInt)

    val startSum = calcFn(population, 0)
    val endPos = population.max

    def calc(minSum: Int, index: Int): Int = {
      if (index > endPos) {
        minSum
      } else {
          val sum = calcFn(population, index)
          if (sum > minSum) minSum
          else calc(sum, index + 1)
      }
    }
    calc(startSum, 1)
  }

  def calcFuelLineary(population: Seq[Int], index: Int) =
    population.map(pos => Math.abs(pos - index)).sum

  def calcFuelExponentally(population: Seq[Int], index: Int) =
    population.map(pos => calFuelDistance(Math.abs(pos - index))).sum

  def calFuelDistance(distance: Int) = distance * (distance + 1) / 2

  if (day == 7) {
    val round1 = p2021_7(
        "input_7.txt",
        calcFuelLineary
      )
    val round2 = p2021_7(
        "input_7.txt",
        calcFuelExponentally
      )
    println(s"$round1, $round2")
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
}
