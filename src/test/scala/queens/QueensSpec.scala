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

    it("all solution log") {
      allSolutionLog(4, 4) should contain theSameElementsAs
        Seq(Seq(2, 0, 3, 1), Seq(1, 3, 0, 2))
    }
  }
}

