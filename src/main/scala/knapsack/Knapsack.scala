package knapsack

import cats.effect.IO
import org.scalameter.api._
import scala.collection._

object KnapsackImp {
  def knapsack(maxWeight: Int, values: Seq[Int], weights: Seq[Int]): Int =
  {
      val n = values.length
      var solutions: Array[Int] = Array.fill(maxWeight + 1)( 0 )
      (1 to n) foreach { i =>
          val newSolutions = Array.fill(maxWeight + 1)( 0 )
          (1 to maxWeight) foreach { j =>
              newSolutions(j) = if( j - weights(i-1) >= 0 ) {
                  Math.max(
                      solutions(j) ,
                      solutions(j - weights(i-1)) + values(i-1)
                  )
              } else {
                  solutions(j)
              }
          }
          solutions = newSolutions
      }
      solutions(maxWeight)
  }
}

object KnapsackFP {
  def knapsack(maxWeight: Int, values: Seq[Int], weights: Seq[Int]): Int =
  {
    def M(i: Int, j: Int): IO[Int] = {
      if (i < 0) {
        IO(0)
      } else {
        val value1 = M(i-1, j)
        if (j >= weights(i)) {
          val value2 = M(i-1, j-weights(i)).map(_ + values(i))
          for {
            v1 <- value1
            v2 <- value2
          } yield Math.max(v1, v2)
        } else {
          value1
        }
      }
    }

    M(values.length-1, maxWeight).unsafeRunSync
  }
}

object KnapsackMemo {
  def memoize[I, O](f: I => O): I => O = new mutable.HashMap[I, O]() {
      override def apply(key: I) = getOrElseUpdate(key, f(key))
  }

  def knapsack(maxWeight: Int, values: Seq[Int], weights: Seq[Int]): Int =
  {
    lazy val M: ((Int, Int)) => Int = memoize {
      case (i, _) if (i < 0) =>
        0
      case (i, j) =>
        val value1 = M(i-1, j)
        if (j >= weights(i)) {
          val value2 = M(i-1, j-weights(i)) + values(i)
          Math.max(value1, value2)
        } else {
          value1
        }
    }

    M(values.length-1, maxWeight)
  }
}

//*
object Knapsack extends Bench.LocalTime {
  val random = new scala.util.Random(1000)
  val length = random.nextInt(10) + 10
  val values = (1 to length) map { j => random.nextInt(10) + 1 }
  val weights = (1 to length) map { j => random.nextInt(10) + 1 }

  //val length = 9
  //val values = Vector(3, 3, 5, 3, 7)
  //val weights = Vector(1, 2, 4, 2, 3)

  val retImp = KnapsackImp.knapsack(length, values, weights)
  val retFP = KnapsackFP.knapsack(length, values, weights)
  val retMemo = KnapsackMemo.knapsack(length, values, weights)

  println(s"result Imp: $retImp, FP: $retFP, Memo: $retMemo")
  //println(s"result Imp: $retImp, Memo: $retMemo")

  if (true) {

  val sizes = Gen.range("size")(1, 10, 1)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Knapsack" in {
    measure method "Imperative" in {
      using(ranges) in {
        r => r.foreach( i => KnapsackImp.knapsack(length, values, weights))
      }
    }

    measure method "FP" in {
      using(ranges) in {
        r => r.foreach(i => KnapsackFP.knapsack(length, values, weights))
      }
    }

    measure method "Memo" in {
      using(ranges) in {
        r => r.foreach(i => KnapsackMemo.knapsack(length, values, weights))
      }
    }
  }
  }
}
//*/
