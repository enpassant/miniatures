package queens

import org.scalatest._
import org.scalatest.Matchers._

class QueensSpec extends FunSpec with Matchers {
  import Queens._

  describe("4 Queens") {
    it("all solution") {
      allSolution(4, 4) should contain theSameElementsAs
        Seq(Seq(2, 0, 3, 1), Seq(1, 3, 0, 2))
    }

    it("nothing to do") {
      lazy val solutions = allSolutionLog(4, 4)
    }

    it("all solution log") {
      lazy val solutions = allSolutionLog(4, 4)
      println("Searching first solution")
      solutions.take(1).toList
      println("Searching two solutions")
      solutions.take(2) should contain theSameElementsAs
        Seq(Seq(2, 0, 3, 1), Seq(1, 3, 0, 2))
      println("Searching three solutions")
      solutions.take(3).toList
    }
  }
}

