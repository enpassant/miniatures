package com.example

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
}

object DatabaseAPI {
  type GetItem = String => Either[String, Item]
  type SaveOrder = Order => Either[String, Order]
}

object MemDatabase {
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
  val saveOrder: SaveOrder = order => Right(order)
  val saveOrderFailing: SaveOrder = order => Left("No space on disk!")
}

object Store {
  import DatabaseAPI._
  import StoreAPI._

  private def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  val placeOrderBusinessLogic =
    (getItem: GetItem, saveOrder: SaveOrder) =>
      (request: OrderRequest) =>
  {
      val sumList = request.order.items.map {
        orderedItem => getItem(orderedItem.itemId).right map {
          _.price * orderedItem.quantity }
      }
      val savedOrder = sequence(sumList).right flatMap {
        sl => saveOrder(request.order).right map (_ => sl.sum)
      }
      OrderResponse(savedOrder)
  }
}

object FuncDI extends App {
  import Store._
  import StoreAPI._
  import DatabaseAPI._
  import MemDatabase._

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

  val placeOrderMemDB = placeOrderBusinessLogic(getItem, saveOrder)

  println(placeOrderMemDB(orderRequest1).toString)
  println(placeOrderMemDB(orderRequest2).toString)

  val placeOrderMemDBFailing = placeOrderBusinessLogic(
    getItem,
    saveOrderFailing)

  println(placeOrderMemDBFailing(orderRequest1).toString)
  println(placeOrderMemDBFailing(orderRequest2).toString)

  def makeLogged[I, O](fn: I => O)(input: I): O = {
    println(s"LOG. input: $input")
    val output = fn(input)
    println(s"LOG. output: $output")
    output
  }
  val placeOrderLogged = placeOrderBusinessLogic(
    makeLogged(getItem),
    makeLogged(saveOrder))

  println(placeOrderLogged(orderRequest1).toString)
  println(placeOrderLogged(orderRequest2).toString)
}
