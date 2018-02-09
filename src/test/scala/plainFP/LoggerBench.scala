package plainFP

import org.scalameter.api._
import plainFP._

object LoggerBench extends Bench.ForkedTime {
  val ranges = for {
    size <- Gen.range("size")(20000, 100000, 20000)
  } yield 0 until size

  performance of "MonadLogger" in {
    measure method "main" in {
      using(ranges) in {
        _.map(i => MonadLogger.main(Array("-t")))
      }
    }
  }

  performance of "Logger" in {
    measure method "main" in {
      using(ranges) in {
        _.map(i => Logger.main(Array("-t")))
      }
    }
  }
}
