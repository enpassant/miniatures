package plainFP

import scala.collection.mutable.ListBuffer

object Logger extends App {
  val silent = args.contains("-t")

  class Logger(id : String) {
    private val log = new ListBuffer[String]
    def println(s: String): Unit = log += s
    def dump(): Unit = if (!silent) {
      log.foreach(entry => Console.println(s"$id: $entry"))
    }
  }

  type Logged[T] = Logger => T

  def logContext[T](logger: Logger)(op: Logged[T]) = {
    op(logger)
    logger.dump()
  }

  val logger1 = new Logger("log1")
  val logger2 = new Logger("log2")

  def fact(x: Int): Logged[Int] = { log =>
    log.println(s"fact: $x")
    logContext(logger2) { log2 =>
      log2.println("log in here is different!")
      WebTools.wget(s"http://catpics.net/pic$x")(log2)
    }
    if (x == 1) 1 else x * fact(x-1)(log)
  }

  logContext(logger1) { log1 =>
    val in = 3
    val out = fact(in)(log1)
    if (!silent) println(s"fact($in): $out")
  }
}

object WebTools {
  import Logger.Logged
  def wget(url: String): Logged[Unit] = { log =>
    log.println(s"Downloading $url")
  }
}
