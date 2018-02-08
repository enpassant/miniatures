import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MyDAG {
  def x = 5
  def y = 6
  def z = x + y
  def zz = z
}

trait MyPatchedDAG extends MyDAG {
  override def y = 7
}

case class Build(
  x: Int,
  y: Int,
  z: () => Int
)

case class Build2(
  build: Build,
  zz: () => Int
)

object MyBuild {
  def build(
    x: Int = 5,
    y: Int = 6,
    z: (Int, Int) => Int = (x, y) => x + y
  ) : Build = {
    Build(x, y, () => z(x, y))
  }

  def build2(
    b: Build,
    zz: (() => Int) => Int = z => z()
  ): Build2 = {
    Build2(b, () => zz(() => b.z()))
  }
}

case class BuildF(
  x: Future[Int],
  y: Future[Int],
  z: () => Future[Int]
)

case class BuildF2(
  build: BuildF,
  zz: () => Future[Int]
)

object MyBuildF {
  def build(
    x: Future[Int] = Future{ 5 },
    y: Future[Int] = Future { 6 },
    z: (Future[Int], Future[Int]) => Future[Int] = (xF, yF) => {
      for {
        x <- xF
        y <- yF
      } yield (x + y)
    }
  ) : BuildF = {
    BuildF(x, y, () => z(x, y))
  }

  def build2(
    b: BuildF,
    zz: (() => Future[Int]) => Future[Int] = z => z()
  ): BuildF2 = {
    BuildF2(b, () => zz(() => b.z()))
  }
}
