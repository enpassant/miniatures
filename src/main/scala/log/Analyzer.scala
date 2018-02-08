package log.anal

import java.io._
import monix.eval.Task
import monix.execution._
import monix.execution.atomic._
import monix.execution.Scheduler.Implicits.global
import monix.reactive._
import monix.reactive.OverflowStrategy._
import monix.reactive.observers._
import monix.reactive.subjects._
import scala.concurrent.duration._
import scala.concurrent._

object Analyzer extends App {
  def aggregatePipe[I,O](s: O)(fn: (O, I) => Either[O,O]) = new Pipe[I,O] {
    override def unicast: (Observer[I], Observable[O]) = {
      val (in, out) =
        Observable.multicast[O](MulticastStrategy.Publish, Unbounded)(global)

      var state: O = s

      val observer = new Observer[I] {
        override def onError(ex: Throwable) = {}
        override def onComplete() = { }
        override def onNext(elem: I) = {
          fn(state, elem) match {
            case Left(o) =>
              state = o
              Ack.Continue
            case Right(o) =>
              val ack = in.onNext(state)
              state = o
              ack
          }
        }
      }

      (observer, out)
    }
  }

  //def parseLine(line: String): String = {
    //val parts = line.split("\\s+")
    //val items = parts(0).split("\\|")
    //if (items.length > 5) items(5) + " " + line else line
  //}

  //val file = new File("/home/kalman/Nyilvános/server.log_2018-01-11T08-07-54")
  //val reader = new BufferedReader(new FileReader(file))

  //val lines = Observable.fromLinesReader(reader)
    //.filter(_.trim.length != 0)
    //.map(parseLine)
    //.pipeThrough(new AggregatePipe("", (acc: String, line: String) =>
      //if (line.startsWith("_ThreadID")) Right(line) else Left(acc + line)
      //))
  //lines.foreach(println)

  val consumer: String => Long => Future[Ack] = title => value => Future {
    val max = Math.max(100 - 3 * value.toInt, 20)
    val wait = scala.util.Random.nextInt(max) * 10
    println(s"$title($wait): $value")
    Thread.sleep(wait)
    Ack.Continue
  }

  val kafka = Observable.interval(200.milliseconds)
  //.pipeThrough(
    //aggregatePipe(0L) { (acc: Long, value: Long) =>
      //if (value % 5 == 0) Right(value) else Left(value + acc)
  //})
  .pipeThrough(
    aggregatePipe(List.empty[Long]) { (acc: List[Long], value: Long) =>
      if (value % 5 == 0) Right(List(value)) else Left(value :: acc)
  })

  //kafka.asyncBoundary(DropNew(20000)).subscribe(consumer("First"))
  //kafka.asyncBoundary(DropNew(20000)).subscribe(consumer("Second"))
  //kafka.asyncBoundary(DropNew(20000)).subscribe(consumer("Third"))
  kafka.foreach(println)

  Thread.sleep(10000)
}
