package plainFP

import independence._
import independence.Level._
import independence.ResultOps._

object MonadLogger {
  def fact(x: Int): Result[Int] = for {
    logFact <- log(INFO, "log1", GoodResult(s"fact: $x"))
    logDiff <- log(DEBUG, "log2", GoodResult("log in here is different!"))
    response <- MonadWebTools.wget(s"http://catpics.net/pic$x")
    factorial <- if (x == 1) GoodResult(1) else fact(x-1).map(_ * x)
  } yield factorial

  def main(args: Array[String]): Unit = {
    val in = 3
    val result = fact(in)
    if (!args.contains("-t")) {
      result.processInfos(processLog("log2"))
      result.map(out => println(s"fact($in): $out"))
      result.processInfos(processLog("log1"))
    }
  }

  def processLog(place: String)(logs: Vector[Log]) = {
    logs foreach {
      case log @ Log(level, place_) if place == place_ =>
        println(f"/LOG/ [$level%5s] |$place%4s| ${log.message()}")
      case _ =>
    }
    Result(())
  }
}

object MonadWebTools {
  def wget(url: String): Result[String] = {
    log(INFO, "log2", GoodResult(s"Downloading $url"))
  }
}
