import scala.collection.mutable.ListBuffer

object Logger {
  class Logger(id : String) {
    private val log = new ListBuffer[String]
    def println(s: String): Unit = log += s
    def dump(): Unit =
      log.foreach(entry => Console.println(s"$id: $entry"))
  }

  type Logged[T] = Logger => T

  def logContext[T](logger: Logger)(op: Logged[T]) = {
    op(logger)
    logger.dump()
  }

  def fact(x: Int): Logged[Int] = { log =>
    log.println(s"fact: $x")
    logContext(new Logger("log2")) { log2 =>
      log2.println("log in here is different!")
      WebTools.wget(s"http://catpics.net/pic$x")(log2)
    }
    if (x == 1) 1 else x * fact(x-1)(log)
  }

  def main(args: Array[String]) = {
    logContext(new Logger("log1")) { log1 =>
      val in = 3
      val out = fact(in)(log1)
      println(s"fact($in): $out")
    }
  }
}

object WebTools {
  import Logger.Logged
  def wget(url: String): Logged[Unit] = { log =>
    log.println(s"Downloading $url")
  }
}
