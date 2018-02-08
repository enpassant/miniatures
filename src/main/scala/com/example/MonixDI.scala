package com.example.monix

import monix.eval.Task
import monix.execution._
import monix.execution.atomic._
import monix.execution.Scheduler.Implicits.global
import monix.reactive._
import monix.reactive.OverflowStrategy._
import monix.reactive.observers._
import monix.reactive.subjects._
import scala.concurrent.duration._

case class Item(name: String, price: Double)
case class OrderedItem(itemId: String, quantity: Double)
case class Order(id: String, customer: String, items: List[OrderedItem])

case class PublishedRequest(value: Any, f: Any => Any)

trait DBCommand
case class GetItem(id: String) extends DBCommand
case class SaveOrder(order: Order) extends DBCommand

trait DBEvent
case class ItemReturned(id: String, item: Item) extends DBEvent

object StoreAPI {
  case class OrderRequest(order: Order)
  case class OrderResponse(status: Either[String, Double])

  type PlaceOrder = OrderRequest => Task[OrderResponse]

  trait IStore {
    def placeOrder: PlaceOrder
  }
}

object DatabaseAPI {
  type GetItem = String => Task[Either[String, Item]]
  type SaveOrder = Order => Task[Either[String, Order]]

  trait IDatabase {
    def getItem: GetItem
    def saveOrder: SaveOrder
  }
}

case class Store(db: DatabaseAPI.IDatabase) extends StoreAPI.IStore {
  import DatabaseAPI._
  import StoreAPI._

  private def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  private def ete[A,B](v: Either[A,Task[Either[A,B]]]): Task[Either[A,B]] = {
    v match {
      case Left(a) => Task(Left(a))
      case Right(task) => task
    }
  }

  val placeOrder =
      (request: OrderRequest) =>
  {
      val sumList = Task.sequence(request.order.items.map {
        orderedItem => db.getItem(orderedItem.itemId).map(_.right map {
          _.price * orderedItem.quantity })
      })
      val savedOrder = sumList.map(sequence(_).right map {
        sl => db.saveOrder(request.order).map(_.right map (_ => sl.sum))
      }).flatMap(ete)
      savedOrder.map(OrderResponse(_))
  }
}

abstract class MemDatabase() extends DatabaseAPI.IDatabase {
  val items = Map(
    "001" -> Item("Sword", 399.9),
    "002" -> Item("Chair", 24.9),
    "003" -> Item("Table", 99.9),
    "004" -> Item("Bed", 59.9)
  )

  val getItem: DatabaseAPI.GetItem = id => Task(items get id match {
    case None => Left(s"Missing item $id!")
    case Some(item) => Right(item)
  })
}

case class MemDatabaseGood() extends MemDatabase {
  val saveOrder: DatabaseAPI.SaveOrder = order => Task(Right(order))
}

object MonixDI extends App {
  import StoreAPI._
  import DatabaseAPI._

  type MessageHandler = PartialFunction[Any, Any]

  val io = Scheduler.io(name="engine-io")
  case class Blocking(task: Task[_], scheduler: Scheduler = io)

  val (messageBusIn, messageBusOut) =
    Observable.multicast[Any](MulticastStrategy.Publish, DropNew(20000))(global)

  case class KafkaMessage(topic: String, message: Any)

  val kafkaUser = messageBusOut.collect {
    case KafkaMessage("user", message) => message
  }

  kafkaUser.dump("KAFKA user: ").subscribe()

  makeEffect(KafkaMessage("user", "User message 1"))
  makeEffect(KafkaMessage("user", "User message 2"))

  def subscribe(fn: MessageHandler) = {
    messageBusOut.subscribe { message =>
      val (msg, fOpt) = message match {
        case PublishedRequest(value, f) =>
          (value, Some(f))
        case msg =>
          (msg, None)
      }

      if (fn.isDefinedAt(msg)) {
        val (taskOpt, scheduler): (Option[Task[_]], Option[Scheduler]) =
          fn(msg) match
        {
          case () => (None, None)
          case Blocking(task, sch) =>
            (Some(task), Some(sch))
          case task: Task[_] =>
            (Some(task), None)
          case result =>
            (Some(Task(result)), None)
        }

        taskOpt map { task =>
          val sch = scheduler getOrElse global
          fOpt match {
            case Some(f) =>
              task.map(f).runAsync(sch)
            case _ =>
              task.map(messageBusIn.onNext(_)).runAsync(sch)
          }
        }
      }
      Ack.Continue
    }
  }

  def makeEffect(value: Any) = {
    messageBusIn.onNext(value)
  }

  def makeEffect(value: Any, f: Any => Any) = {
    messageBusIn.onNext(PublishedRequest(value, f))
  }

  val getItem: MessageHandler = {
    case GetItem(id) =>
      println(s"GetItem: $id")
      ItemReturned(id, Item("Milk", 12.4))
  }

  val processItem: MessageHandler = {
    case ItemReturned(id, item) => println(s"Process item $id: $item")
  }

  val count = AtomicInt(0)
  val countProcessing: MessageHandler = {
    case ItemReturned(id, item) =>
      count.increment()
  }

  //subscribe(getItem)
  //subscribe(processItem)

  subscribe(getItem orElse processItem)

  (1 to 100000) map { i => subscribe(countProcessing) }

  val tasks = (1 to 10) map { idx => Task {
    makeEffect(GetItem(s"$idx"))
  }}

  case class CalcFib(count: Int, a: Int, b: Int)

  val fib: MessageHandler = {
    case CalcFib(count, a, b) if count > 1 => CalcFib(count-1, b, a + b)
    case CalcFib(count, a, b) => println("Fibonaccy result: " + b)
  }

  subscribe(fib)
  makeEffect(CalcFib(40, 0, 1))
  makeEffect(CalcFib(10, 0, 1))

  //val callFib: MessageHandler = {
    //case f: Fib => makeEffect(f, callFib)
  //}

  //callFib(Fib(40, 0, 1))

  makeEffect(
    GetItem(s"111"),
    result => {
      println(s"Async result: $result")
      Thread.sleep(100)
      println(s"Async result: $result END")
    }
  )

  case object ReadLine
  case class PrintLine(line: String)

  case class LineRead(line: String)

  val consoleIO: MessageHandler = {
    case ReadLine =>
      Blocking(Task(LineRead(scala.io.StdIn.readLine()))
        .timeout(2.seconds)
        .onErrorHandleWith(error => Task(println("Name input cancelled"))))
    case PrintLine(line) =>
      println(line)
  }

  val commands = Atomic(List.empty[Any])
  val consoleTest: MessageHandler = {
    case ReadLine =>
      commands.transform(ReadLine +: _)
      LineRead("Feca")
    case command: PrintLine =>
      commands.transform(command +: _)
  }

  subscribe(consoleIO)
  subscribe(consoleTest)

  makeEffect(PrintLine("What is your name?"))
  makeEffect(ReadLine,
    { case LineRead(name) => makeEffect(PrintLine(s"Hello $name!")) })

  val list = Task.gather(tasks.toSeq)
  list.runAsync.foreach(println)

  (messageBusOut.scan(0) { case (a, b) => a + 1 }).bufferTumbling(5).subscribe {
    msg => println(msg); Ack.Continue
  }

  val orderRequest1 = OrderRequest(Order("ORD_01", "IBM",
    List(
      OrderedItem("002", 12.0),
      OrderedItem("004", 2.0)
    )))

  val orderRequest2 = OrderRequest(Order("ORD_02", "Google",
    List(
      OrderedItem("002", 12.0),
      OrderedItem("005", 2.0)
    )))

  val placeOrder: DatabaseAPI.IDatabase => MessageHandler = db => {
    case request: OrderRequest =>
      Task.sequence(request.order.items.map {
        orderedItem => db.getItem(orderedItem.itemId).map(_.right map {
          _.price * orderedItem.quantity })
      })
      //val savedOrder = sumList.map(sequence(_).right map {
        //sl => db.saveOrder(request.order).map(_.right map (_ => sl.sum))
      //}).flatMap(ete)
      //savedOrder.map(OrderResponse(_))
  }

  //messageBusOut.dump("Dump: ").subscribe()
  subscribe(placeOrder(MemDatabaseGood()))
  makeEffect(orderRequest1)
  makeEffect(orderRequest2)

  Thread.sleep(3000)
  messageBusIn.onComplete
  println(s"count: $count")
  println(s"commands: ${commands.get.reverse}")
}
