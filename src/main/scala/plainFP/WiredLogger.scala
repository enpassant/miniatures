import scala.collection.mutable.ListBuffer

object WiredLogger {
  class Logger(id : String) {
    private val log = new ListBuffer[String]
    def println(s: String): Unit = log += s
    def dump(): Unit =
      log.foreach(entry => Console.println(s"$id: $entry"))
  }

  def factBase(log: Logger)(x: Int): Int = {
    log.println(s"fact: $x")
    wget(s"http://catpics.net/pic$x")
    if (x == 1) 1 else x * factBase(log)(x-1)
  }

  val log1 = new Logger("log1")
  val log2 = new Logger("log2")
  val fact = factBase(log1) _
  val wget = { url: String =>
    log2.println("log in here is different!")
    WiredWebTools.wgetBase(log2)(url)
  }

  def main(args: Array[String]) = {
    val in = 3
    val out = fact(in)
    log2.dump()
    println(s"fact($in): $out")
    log1.dump()
  }
}

object WiredWebTools {
  import WiredLogger.Logger
  def wgetBase(log: Logger)(url: String): Unit = {
    log.println(s"Downloading $url")
  }
}
