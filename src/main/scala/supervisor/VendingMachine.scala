package supervisor

import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

trait CoffeeType { def price: Int; def quantity: Int }
case object BlackCoffee extends CoffeeType { val price = 1; val quantity = 1 }
case object Latte extends CoffeeType { val price = 3; val quantity = 3 }
case object Espresso extends CoffeeType { val price = 2; val quantity = 2 }

trait Command
case class Coins(number: Int) extends Command
case class Selection(coffee: CoffeeType) extends Command

case class Beverage(coffee: CoffeeType)

case class NotEnoughCoinsError(message: String)

case class OutOfCoffeeBeansFailure(
  customer: ActorRef,
  pendingOrder: Selection,
  nrOfInsertedCoins: Int
) extends Exception

case class AddCustomer(name: String, coffee: CoffeeType)
case class AddedCustomer(customer: ActorRef)

case class StartShopping(customer: ActorRef)

case class SetState(coins: Int)

case object WaitForCustomer
case class OutOfOrder(duration: FiniteDuration)
case object Repaired

class Root extends Actor with ActorLogging with ScheduledEvent {
  import context.dispatcher

  val simulator = VendingMachine.createSimulator
  context.watch(simulator)

  def receive = {
    case Terminated(actorRef) =>
      self ! PoisonPill
  }

  override def postStop() {
    log.info("Stopping root actor")
    context.system.terminate
  }

  addEvent(1.second, simulator, AddCustomer("John", Latte))
  addEvent(3.second, simulator, AddCustomer("Jane", Latte))
  addEvent(4.second, simulator, AddCustomer("George", Espresso))
  addEvent(8.second, simulator, AddCustomer("Jim", Latte))
  addEvent(10.second, simulator, AddCustomer("Sue", BlackCoffee))
  addEvent(13.second, simulator, AddCustomer("Fred", Espresso))
}

class Simulator extends Actor with ActorLogging with ScheduledEvent {
  import context.dispatcher

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      case e @ OutOfCoffeeBeansFailure(customer, pendingOrder, coins) =>
        log.info(s"ServiceGuy notified: $e")
        customer.tell(Coins(coins), machine)
        machine.tell(OutOfOrder(10.seconds), customer)
        addEvent(5.seconds, machine, Repaired)
        Restart
      case _: Exception =>
        Escalate
    }

  val machine = VendingMachine.createMachine

  startWaiting

  def receive = {
    case _ =>
  }

  def waitForCustomer(cancellable: Cancellable): Receive = {
    case AddCustomer(name, coffee) =>
      cancellable.cancel
      log.info("Customer {} arrived", name)
      val customer = VendingMachine.createCustomer(name, coffee)
      context.watch(customer)
      customer ! StartShopping(machine)
      context become shop(Vector())
  }

  def shop(customers: Vector[ActorRef]): Receive = {
    case Terminated(customer) if customers.isEmpty =>
      startWaiting
    case Terminated(customer) =>
      val customer = customers.head
      context become shop(customers.tail)
      context.watch(customer)
      customer ! StartShopping(machine)
    case AddCustomer(name, coffee) =>
      val customer = VendingMachine.createCustomer(name, coffee)
      context become shop(customers :+ customer)
  }

  def startWaiting = {
    log.info("Start waiting for customer")
    context become waitForCustomer(addEvent(5.second, self, PoisonPill))
  }
}

class CoffeeMachine extends Actor with ActorLogging {
  var nrOfInsertedCoins = 0
  var totalNrOfCoins = 0
  var quantity = 7

  def receive = {
    case Coins(nr) =>
      nrOfInsertedCoins += nr
      totalNrOfCoins += nr
      log.info("Inserted [{}] coins", nr)
      log.info("Total number of coins in machine is [{}]", totalNrOfCoins)

    case selection @ Selection(coffeeType)
      if (nrOfInsertedCoins < coffeeType.price) =>
        sender !
          NotEnoughCoinsError(
            s"Please insert [${coffeeType.price - nrOfInsertedCoins}] coins")
    case selection @ Selection(coffeeType)
      if (quantity < coffeeType.quantity) =>
          throw new OutOfCoffeeBeansFailure(
            sender,
            selection,
            nrOfInsertedCoins)
    case selection @ Selection(coffeeType) =>
        log.info("Brewing your {}", coffeeType)
        if (nrOfInsertedCoins > coffeeType.price) {
          sender ! Coins(nrOfInsertedCoins - coffeeType.price)
        }
        sender ! Beverage(coffeeType)
        nrOfInsertedCoins = 0
        quantity -= coffeeType.quantity

    case SetState(coins) =>
      nrOfInsertedCoins = coins

    case message @ OutOfOrder(duration) =>
      context become waitForRepair(duration.fromNow)
      self.forward(message)
  }

  def waitForRepair(deadline: Deadline): Receive = {
    case Repaired =>
      log.info("The machine has repaired")
      context become receive
    case _ =>
      sender ! OutOfOrder(deadline.timeLeft)
  }

  override def postRestart(failure: Throwable): Unit = {
    log.info("Restarting coffee machine...")
  }
}

class Customer(name: String, coffee: CoffeeType)
  extends Actor
  with ActorLogging
  with ScheduledEvent
{
  import context.dispatcher

  def receive = waitForMachine

  def waitForMachine: Receive = {
    case StartShopping(coffeeMachine) =>
      addEvent(100.milliseconds, coffeeMachine, Coins(2))
      addEvent(200.milliseconds, coffeeMachine, Selection(coffee))
      context become shop(coffeeMachine)
  }

  def shop(coffeeMachine: ActorRef): Receive = {
    case Coins(coins) =>
      log.info("Got myself [{}] coins back", coins)
    case Beverage(coffee) =>
      log.info("Got myself an {}", coffee)
      context.stop(self)
    case NotEnoughCoinsError(message) =>
      log.info("Got myself a validation error: {}", message)
      addEvent(100.milliseconds, coffeeMachine, Coins(2))
      addEvent(200.milliseconds, coffeeMachine, Selection(coffee))
    case message @ OutOfOrder(duration) =>
      log.info(
        "Got myself a failure: The machine is out of order for {} minutes",
        duration.toSeconds)
      if (Random.nextInt(100) < 90) {
        context.actorSelection("../..").resolveOne(1.second).foreach {
          grandParent =>
            addEvent(3.seconds, grandParent, AddCustomer(name, coffee))
        }
      }
      addEvent(1.second, self, PoisonPill)
      context become waitForLeave
  }

  def waitForLeave: Receive = {
    case Coins(coins) =>
      log.info("Got myself [{}] coins back", coins)
    case msg =>
      log.info("Missed message: {}", msg)
  }

  override def postStop(): Unit = {
    log.info("Customer {} leave", name)
  }
}

object VendingMachine extends App {
  def createSimulator(implicit context: ActorContext) =
    context.actorOf(Props[Simulator], "simulator")

  def createMachine(implicit context: ActorContext) =
    context.actorOf(Props[CoffeeMachine], name = "coffeeMachine")

  def createCustomer(name: String, coffee: CoffeeType)
    (implicit context: ActorContext) =
      context.actorOf(Props(new Customer(name, coffee)), "customer-" + name)

  val system = ActorSystem("vendingMachineDemo")
  val root = system.actorOf(Props[Root], "root")
}

trait ScheduledEvent {
  def context: ActorContext
  def self: ActorRef

  def addEvent(duration: FiniteDuration, actor: ActorRef, message: Any)
    (implicit ec: ExecutionContext) = {
      context.system.scheduler.scheduleOnce(duration) {
        actor.tell(message, self)
      }
    }
}
