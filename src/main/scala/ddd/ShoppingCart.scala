package ddd

import Types._
import ShoppingCartAPI._
import ErrorAPI._
import PaymentAPI._

import scala.util.Random

object ShoppingCart {
  val addToEmpty: AddToEmpty = {
    case (EmptyCart, AddItem(item)) =>
      Right(List(FirstItemAdded(item)))
  }

  val addToActive: AddToActive = {
    case (ActiveCart(unpaidItems), AddItem(item)) =>
      if (unpaidItems.contains(item)) {
        Left(ItemAlreadyAdded(item))
      } else {
        Right(List(NextItemAdded(item)))
      }
  }

  val removeFromActive: RemoveFromActive = {
    case (ActiveCart(unpaidItems), RemoveItem(item)) =>
      if (!unpaidItems.contains(item)) {
        Left(NoItemInCart(item))
      } else if (unpaidItems.size > 1) {
        Right(List(AnItemRemoved(item)))
      } else {
        Right(List(LastItemRemoved(item)))
      }
  }

  val payActive: PayActive = {
    case (ActiveCart(unpaidItems), Pay(payment)) =>
      payment match {
        case card: Card if (Random.nextInt(100) < 30) =>
          Left(CardProcessFailure(card))
        case _ =>
          Right(List(Paid(payment)))
      }
  }

  val handleFirstItemAdded: HandleFirstItemAdded = {
    case (EmptyCart, FirstItemAdded(item)) =>
      Right(ActiveCart(List(item)))
  }

  val handleNextItemAdded: HandleNextItemAdded = {
    case (ActiveCart(unpaidItems), NextItemAdded(item)) =>
      Right(ActiveCart(unpaidItems :+ item))
  }

  val handleAnItemRemoved: HandleAnItemRemoved = {
    case (ActiveCart(unpaidItems), AnItemRemoved(item)) =>
      Right(ActiveCart(unpaidItems filter (i => i != item)))
  }

  val handleLastItemRemoved: HandleLastItemRemoved = {
    case (ActiveCart(unpaidItems), LastItemRemoved(item)) =>
      Right(EmptyCart)
  }

  val handlePaid: HandlePaid = {
    case (ActiveCart(unpaidItems), Paid(payment)) =>
      Right(PaidCart(unpaidItems, payment))
  }

  val getEmptyCartCaps: GetCapabilities = {
    case state @ EmptyCart =>
      Map("AddItem" ->
        createCapability(state, addToEmpty, handleFirstItemAdded))
  }

  val getActiveCartCaps: GetCapabilities = {
    case state @ ActiveCart(unpaidItems) =>
      Map(
        "AddItem" ->
          createCapability(state, addToActive, handleNextItemAdded),
        "Pay" -> createCapability(state, payActive, handlePaid)
      ) ++
      unpaidItems.map { item =>
        "RemoveItem_" + item.id -> createCapability(
          state,
          RemoveItem(item),
          removeFromActive,
          handleAnItemRemoved orElse handleLastItemRemoved
        )
      }.toMap
  }

  val getPaidCartCaps: GetCapabilities = {
    case PaidCart(state, payment) =>
      Map()
  }
}
