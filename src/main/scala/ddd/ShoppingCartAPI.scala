package ddd

import Types._

object ShoppingCartAPI {
  import PaymentAPI._

  case class Item(id: ID, name: String80, qty: Quantity, price: Money)

  sealed trait ShoppingCart extends State

  case object EmptyCart extends ShoppingCart
  case class ActiveCart(unpaidItems: List[Item]) extends ShoppingCart

  case class PaidCart(
    paidItems: List[Item],
    payment: Payment
  ) extends ShoppingCart

  case class AddItem(item: Item) extends Command
  case class RemoveItem(item: Item) extends Command
  case class Pay(payment: Payment) extends Command

  case class FirstItemAdded(item: Item) extends Event
  case class NextItemAdded(item: Item) extends Event
  sealed trait ItemRemoved extends Event
  case class AnItemRemoved(item: Item) extends ItemRemoved
  case class LastItemRemoved(item: Item) extends ItemRemoved
  case class Paid(payment: Payment) extends Event

  type AddToEmpty = CommandHandler[EmptyCart.type, AddItem, FirstItemAdded]
  type AddToActive = CommandHandler[ActiveCart, AddItem, NextItemAdded]
  type RemoveFromActive = CommandHandler[ActiveCart, RemoveItem, ItemRemoved]
  type PayActive = CommandHandler[ActiveCart, Pay, Paid]

  type HandleFirstItemAdded =
    EventHandler[EmptyCart.type, FirstItemAdded, ActiveCart]
  type HandleNextItemAdded =
    EventHandler[ActiveCart, NextItemAdded, ActiveCart]
  type HandleAnItemRemoved =
    EventHandler[ActiveCart, AnItemRemoved, ActiveCart]
  type HandleLastItemRemoved =
    EventHandler[ActiveCart, LastItemRemoved, EmptyCart.type]
  type HandlePaid =
    EventHandler[ActiveCart, Paid, PaidCart]
}
