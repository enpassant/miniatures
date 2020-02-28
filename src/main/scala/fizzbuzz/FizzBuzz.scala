package fizzbuzz

object Fizzbuzz extends App {
  val factors = Map(3 -> "Fizz", 5 -> "Buzz", 2 -> "Baxx")
  val result = (1 to 30) map { i =>
    factors
      .filter(x => i % x._1  == 0)
      .map(_._2)
      .reduceOption(_ + _)
      .getOrElse(i)
  }
  println(result)
}
