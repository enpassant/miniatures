package queens

object Queens {
  var count = 0

  def permutationSolution(n: Int) = (0 until n).permutations.toStream filter {
    qs =>
      count = count + 1
      qs.zipWithIndex.flatMap {
        case (c, d) => Seq(n + c + d, c - d)
  }.distinct.size == 2*n }

  def allSolution(n: Int, i: Int): Stream[Seq[Int]] =
    if (i <= 0) Stream(Seq()) else allSolution(n, i-1) flatMap extendSolution(n)

  def extendSolution(n: Int)(qs: Seq[Int]) =
    Stream.range(0, n) filter okToAdd(qs) map (q => q +: qs)

  def okToAdd(qs: Seq[Int])(q: Int) =
    qs zip (1 to qs.length) forall notThreaten(q).tupled

  val notThreaten = (qi: Int) => (qj: Int, j: Int) =>
    (qi != qj) && math.abs(qi - qj) != j

  def extendSolutionLog(n: Int)(qs: Seq[Int]) =
    Stream.range(0, n) filter okToAdd(qs) map { q =>
      val ret = q +: qs;
      println(ret)
      ret
    }

  def allSolutionLog(n: Int, i: Int): Stream[Seq[Int]] =
    if (i <= 0)
      Stream(Seq())
    else allSolutionLog(n, i-1) flatMap extendSolutionLog(n)

}
