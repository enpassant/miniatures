package ddd

import Types._
import PaymentAPI._
import ErrorAPI._

object ErrorMessages {
  def toMessage(error: Failure) = error match {
    case ItemAlreadyAdded(item) =>
      s"${item} is already added!"
    case NoItemInCart(item) =>
      s"There is no ${item} in the cart!"
    case CardProcessFailure(card) =>
      s"There was a problem processing your $card card!"

    case UnknownCommand(state, command) =>
      s"Unknown $command command for $state state!"
    case UnknownEvent(state, event) =>
      s"Unknown $event event for $state state!"

    case _ => error.toString
  }
}
