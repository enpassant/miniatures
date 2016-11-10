package queens

object Queens {
  val isGood = (qi: Int) => (qj: Int, j: Int) =>
    (qi != qj) && math.abs(qi - qj) != j

  def okToAdd(qs: Seq[Int])(q: Int) =
    qs zip (1 to qs.length) forall isGood(q).tupled

  def extendSolution(n: Int)(qs: Seq[Int]) =
    (0 until n).view filter okToAdd(qs) map (q => q +: qs)

  def allSolution(n: Int, i: Int): Stream[Seq[Int]] =
    if (i <= 0) Stream(Seq()) else allSolution(n, i-1) flatMap extendSolution(n)

  def extendSolutionLog(n: Int)(qs: Seq[Int]) =
    (0 until n).view filter okToAdd(qs) map { q =>
      val ret = q +: qs;
      println(ret)
      ret
    }

  def allSolutionLog(n: Int, i: Int): Seq[Seq[Int]] =
    if (i <= 0)
      Seq(Seq()).view
    else allSolutionLog(n, i-1) flatMap extendSolutionLog(n)

}
