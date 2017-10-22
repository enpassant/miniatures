package ddd

import Types._
import PaymentAPI._
import ShoppingCartAPI._

object ErrorAPI {
  case class ItemAlreadyAdded(item: Item) extends Error
  case class NoItemInCart(item: Item) extends Error
  case class CardProcessFailure(card: Card) extends Error

  case class UnknownCommand(state: State, command: Command) extends Error
  case class UnknownEvent(state: State, event: Event) extends Error
}
