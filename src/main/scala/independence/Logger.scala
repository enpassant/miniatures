package independence

import Level._

object Logger {
  def asLog[I, O](logger: Logger, level: Level)
    (fn: => O): Result[O] =
  {
    if (logger.isLevelEnabled(level)) {
      val sb = new StringBuilder
      val start = System.nanoTime
      val throwable = new Throwable()
      val stackTrace = throwable.getStackTrace()(2)
      val place = stackTrace.getFileName + ":" + stackTrace.getLineNumber
      try {
        val output = fn
        sb.append(". Output: ").append(output)
        val end = System.nanoTime
        sb.append(". Running time: ").append(end - start).append(" ns")
        GoodResult(output, List(Log(level, place, sb.toString)))
      } catch {
        case e: RuntimeException =>
          sb.append(". Exception: ").append(e.getMessage)
          val end = System.nanoTime
          sb.append(". Running time: ").append(end - start).append(" ns")
          BadResult(ExceptionError(e), List(Log(FATAL, place, sb.toString)))
        case e: Exception =>
          sb.append(". Exception: ").append(e.getMessage)
          val end = System.nanoTime
          sb.append(". Running time: ").append(end - start).append(" ns")
          BadResult(ExceptionError(e), List(Log(ERROR, place, sb.toString)))
      }
    } else {
      GoodResult(fn)
    }
  }

  def makeLog[I, O](logger: Logger, level: Level)
    (fn: I => O): I => Result[O] =
  {
    if (logger.isLevelEnabled(level)) {
      input: I =>
        val sb = new StringBuilder
        val start = System.nanoTime
        val throwable = new Throwable()
        val stackTrace = throwable.getStackTrace()(2)
        val place = stackTrace.getFileName + ":" + stackTrace.getLineNumber
        try {
          sb.append("Input: ").append(input)
          val output = fn(input)
          sb.append(". Output: ").append(output)
          val end = System.nanoTime
          sb.append(". Running time: ").append(end - start).append(" ns")
          GoodResult(output, List(Log(level, place, sb.toString)))
        } catch {
          case e: RuntimeException =>
            sb.append(". Exception: ").append(e.getMessage)
            val end = System.nanoTime
            sb.append(". Running time: ").append(end - start).append(" ns")
            BadResult(ExceptionError(e), List(Log(FATAL, place, sb.toString)))
          case e: Exception =>
            sb.append(". Exception: ").append(e.getMessage)
            val end = System.nanoTime
            sb.append(". Running time: ").append(end - start).append(" ns")
            BadResult(ExceptionError(e), List(Log(ERROR, place, sb.toString)))
        }
    } else {
      input: I => GoodResult(fn(input))
    }
  }
}

trait Logger {
  def isLevelEnabled(level: Level): Boolean
}

