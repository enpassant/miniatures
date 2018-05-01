package com.example

import cats.effect.IO
import cats.data.EitherT
import cats.implicits._

sealed trait Error

case class MissingItem(id: ItemID) extends Error
case object NoSpaceOnDisk extends Error

object result {
  type Result[A] = EitherT[IO, Error, A]

  def makeLogged[I, O](fn: I => Result[O])(input: I): Result[O] = {
    println(s"LOG. input: $input")
    fn(input).flatMap { output: O =>
      println(s"LOG. output: $output")
      EitherT.rightT(output)
    }
  }
}
import result._

case class ItemID(id: String) extends AnyVal
case class OrderID(id: String) extends AnyVal

case class Item(name: String, price: Double)
case class OrderedItem(itemId: ItemID, quantity: Double)
case class Order(id: OrderID, customer: String, items: List[OrderedItem])

object StoreAPI {
  case class OrderRequest(order: Order)
  case class OrderResponse(status: Either[Error, Double])

  type PlaceOrder = OrderRequest => Result[OrderResponse]
}

object DatabaseAPI {
  type GetItem = ItemID => Result[Item]
  type SaveOrder = Order => Result[Order]
}

object MemDatabase {
  import DatabaseAPI._

  val items = Map(
    ItemID("001") -> Item("Sword", 399.9),
    ItemID("002") -> Item("Chair", 24.9),
    ItemID("003") -> Item("Table", 99.9),
    ItemID("004") -> Item("Bed", 59.9)
  )

  val getItem: GetItem = id => {
    items get id match {
      case None => EitherT.leftT(MissingItem(id))
      case Some(item) => EitherT.rightT(item)
    }
  }
  val saveOrder: SaveOrder = order => EitherT.rightT(order)
  val saveOrderFailing: SaveOrder = order => EitherT.leftT(NoSpaceOnDisk)
}

object Store {
  import DatabaseAPI._
  import StoreAPI._

  val getItems: GetItem => OrderRequest => Result[List[(Item, OrderedItem)]] = {
    getItem => request =>
    (request.order.items.map {
      orderedItem => getItem(orderedItem.itemId).map((_, orderedItem))
    }).sequence
  }

  val calcPrice: (Item, OrderedItem) => Double = (item, orderedItem) =>
    item.price * orderedItem.quantity

  val placeOrderBusinessLogic: (GetItem, SaveOrder) => PlaceOrder =
  {
    (getItem, saveOrder) => request =>
    for {
      itemList <- getItems(getItem)(request)
      priceList = itemList.map(calcPrice.tupled)
      _ <- saveOrder(request.order)
    } yield OrderResponse(Right(priceList.sum))
  }
}

case class Config(logged: Boolean)

object FuncDI extends App {
  import Store._
  import StoreAPI._
  import DatabaseAPI._
  import MemDatabase._

  val config: Config = Config(false)

  lazy val getItemFn =
    if (config.logged) makeLogged(getItem) _
    else getItem

  val orderRequest1 = OrderRequest(Order(OrderID("ORD_01"), "IBM",
    List(
      OrderedItem(ItemID("002"), 12.0),
      OrderedItem(ItemID("004"), 2.0)
    )))

  val orderRequest2 = OrderRequest(Order(OrderID("ORD_02"), "Google",
    List(
      OrderedItem(ItemID("002"), 12.0),
      OrderedItem(ItemID("005"), 2.0)
    )))

  val placeOrderMemDB = placeOrderBusinessLogic(getItemFn, saveOrder)

  println(placeOrderMemDB(orderRequest1).value.unsafeRunSync.toString)
  println(placeOrderMemDB(orderRequest2).value.unsafeRunSync.toString)

  val placeOrderMemDBFailing = placeOrderBusinessLogic(
    getItem,
    saveOrderFailing)

  println(placeOrderMemDBFailing(orderRequest1).value.unsafeRunSync.toString)
  println(placeOrderMemDBFailing(orderRequest2).value.unsafeRunSync.toString)

  val placeOrderLogged = placeOrderBusinessLogic(
    makeLogged(getItem),
    makeLogged(saveOrder))

  println(placeOrderLogged(orderRequest1).value.unsafeRunSync.toString)
  println(placeOrderLogged(orderRequest2).value.unsafeRunSync.toString)
}
