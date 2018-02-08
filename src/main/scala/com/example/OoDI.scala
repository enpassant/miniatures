package com.example.oo

import shapeless._
import syntax.singleton._
import record._

case class Item(name: String, price: Double)
case class OrderedItem(itemId: String, quantity: Double)
case class Order(id: String, customer: String, items: List[OrderedItem])

object StoreAPI {
  case class OrderRequest(order: Order)
  case class OrderResponse(status: Either[String, Double])

  type PlaceOrder = OrderRequest => OrderResponse

  trait IStore {
    def placeOrder: PlaceOrder
  }
}

object DatabaseAPI {
  type GetItem = String => Either[String, Item]
  type SaveOrder = Order => Either[String, Order]

  trait IDatabase {
    def getItem: GetItem
    def saveOrder: SaveOrder
  }
}

abstract class MemDatabase() extends DatabaseAPI.IDatabase {
  import DatabaseAPI._

  val items = Map(
    "001" -> Item("Sword", 399.9),
    "002" -> Item("Chair", 24.9),
    "003" -> Item("Table", 99.9),
    "004" -> Item("Bed", 59.9)
  )

  val getItem: GetItem = id => items get id match {
    case None => Left(s"Missing item $id!")
    case Some(item) => Right(item)
  }
}

case class MemDatabaseGood() extends MemDatabase {
  import DatabaseAPI._

  val saveOrder: SaveOrder = order => Right(order)
}

case class MemDatabaseFailing() extends MemDatabase {
  import DatabaseAPI._

  val saveOrder: SaveOrder = order => Left("No space on disk!")
}

case class Store(db: DatabaseAPI.IDatabase) extends StoreAPI.IStore {
  import DatabaseAPI._
  import StoreAPI._

  private def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  val placeOrder =
      (request: OrderRequest) =>
  {
      val sumList = request.order.items.map {
        orderedItem => db.getItem(orderedItem.itemId).right map {
          _.price * orderedItem.quantity }
      }
      val savedOrder = sequence(sumList).right flatMap {
        sl => db.saveOrder(request.order).right map (_ => sl.sum)
      }
      OrderResponse(savedOrder)
  }
}

object OoDI extends App {
  import StoreAPI._
  import DatabaseAPI._

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

  val storeMemDB = Store(MemDatabaseGood())

  println(storeMemDB.placeOrder(orderRequest1).toString)
  println(storeMemDB.placeOrder(orderRequest2).toString)

  val storeMemDBFailing = Store(MemDatabaseFailing())

  println(storeMemDBFailing.placeOrder(orderRequest1).toString)
  println(storeMemDBFailing.placeOrder(orderRequest2).toString)
}
