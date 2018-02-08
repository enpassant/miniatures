package supervisor

import akka.actor._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._

object Main extends App {
  implicit val actorSystem = ActorSystem()
  val supervisor = actorSystem.actorOf(Props[Supervisor], "supervisor")
  supervisor ! StartCache
}

case object StartCache
case object Terminate

class Supervisor extends Actor with ActorLogging {
  override def receive = {
    case StartCache =>
      val cache = context.actorOf(Props[Cache], "cache")
      context.watch(cache)
      (1 to 1000000) foreach { index => cache ! index }
      cache ! PoisonPill

    case Terminated =>
      context.stop(self)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10.seconds) {
      case e: RuntimeException =>
        log.info("Exception occured: {}", e)
        Restart
    }

  override def postStop() {
    log.info("stopping actor")
    context.system.terminate
  }
}

class Cache extends Actor with ActorLogging {
  override def receive = store(Map())

  def store(storage: Map[Int, String]): Receive = {
    case number: Int =>
      if (number % 1000 == 0)
        log.info("{}, mem: {}", number, Runtime.getRuntime.freeMemory)
      if (Runtime.getRuntime.freeMemory < 60000000)
        throw new RuntimeException("Low memory!")
      context become store(storage + (number -> ("".padTo(1000, '*'))))
  }

  override def postStop() {
    log.info("stopping actor")
    System.gc()
    Thread.sleep(100)
  }
}
