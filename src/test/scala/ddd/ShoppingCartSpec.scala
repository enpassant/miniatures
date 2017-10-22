package ddd

import org.scalatest._

import Types._
import PaymentAPI._
import ShoppingCartAPI._
import ShoppingCart._
import ErrorAPI._
import ShoppingCartConfig._
import SampleData._

class ShoppingCartSpec extends FunSpec with Matchers {
  describe("ShoppingCart.") {
    it("Test capabilities") {
      val emptyCart = EmptyCart

      val capabilitiesEmptyCart = getCapabilities(emptyCart)
      capabilitiesEmptyCart should contain key "AddItem"
      val addItemToEmpty = capabilitiesEmptyCart("AddItem")
      val oneItemCartEither = addItemToEmpty(emptyCart, AddItem(itemMilk))
      oneItemCartEither shouldBe Right(
        StateResult(
          ActiveCart(List(itemMilk)),
          List(FirstItemAdded(itemMilk))))
      val oneItemCart = oneItemCartEither.right.get.state

      val capabilitiesOneItemCart = getCapabilities(oneItemCart)
      capabilitiesOneItemCart should (
        contain key ("AddItem") and
        contain key ("RemoveItem") and
        contain key ("PayActive"))
      val removeFromActive = capabilitiesOneItemCart("RemoveItem")
      val emptyCartAgainEither =
        removeFromActive(oneItemCart, RemoveItem(itemMilk))
      emptyCartAgainEither shouldBe Right(
        StateResult(EmptyCart, List(LastItemRemoved(itemMilk))))

      val addNextItem = capabilitiesOneItemCart("AddItem")
      val twoItemCartEither = addNextItem(oneItemCart, AddItem(itemTV))
      twoItemCartEither shouldBe Right(
        StateResult(
          ActiveCart(List(itemMilk, itemTV)),
          List(NextItemAdded(itemTV))))
      val twoItemCart = twoItemCartEither.right.get.state

      val capabilitiesTwoItemCart = getCapabilities(twoItemCart)
      capabilitiesTwoItemCart should (
        contain key ("AddItem") and
        contain key ("RemoveItem") and
        contain key ("PayActive"))

      val removeAnItemFromActive = capabilitiesTwoItemCart("RemoveItem")
      val oneTvItemCartEither =
        removeAnItemFromActive(twoItemCart, RemoveItem(itemMilk))
      oneTvItemCartEither shouldBe Right(
        StateResult(ActiveCart(List(itemTV)),
        List(AnItemRemoved(itemMilk))))

      val payActive = capabilitiesTwoItemCart("PayActive")
      val paidCartEither = payActive(twoItemCart, Pay(mastercard))
      paidCartEither should (
        be (Right(
          StateResult(
            PaidCart(List(itemMilk, itemTV), mastercard),
            List(Paid(mastercard))))
          ) or
        be (Left(CardProcessFailure(mastercard))))
    }

    it("Test command and event handlers") {
      val emptyCart = EmptyCart

      val firstItemAddedResult = commandHandler(emptyCart, AddItem(itemMilk))
      firstItemAddedResult shouldBe Right(List(FirstItemAdded(itemMilk)))

      val oneItemCartEither =
        eventHandler(emptyCart, firstItemAddedResult.right.get.head)
      oneItemCartEither shouldBe Right(ActiveCart(List(itemMilk)))
      val oneItemCart = oneItemCartEither.right.get

      val removeLastFromActiveResult =
        commandHandler(oneItemCart, RemoveItem(itemMilk))
      removeLastFromActiveResult shouldBe Right(List(LastItemRemoved(itemMilk)))
      val emptyCartAgain =
        eventHandler(oneItemCart, removeLastFromActiveResult.right.get.head)
      emptyCartAgain shouldBe Right(EmptyCart)

      val nextItemAddedResult = commandHandler(oneItemCart, AddItem(itemTV))
      nextItemAddedResult shouldBe Right(List(NextItemAdded(itemTV)))

      val twoItemCartEither =
        eventHandler(oneItemCart, nextItemAddedResult.right.get.head)
      twoItemCartEither shouldBe Right(ActiveCart(List(itemMilk, itemTV)))
      val twoItemCart = twoItemCartEither.right.get

      val removeAnItemFromActiveResult =
        commandHandler(twoItemCart, RemoveItem(itemMilk))
      removeAnItemFromActiveResult shouldBe Right(List(AnItemRemoved(itemMilk)))

      val oneTvItemCartEither =
        eventHandler(twoItemCart, removeAnItemFromActiveResult.right.get.head)
      oneTvItemCartEither shouldBe Right(ActiveCart(List(itemTV)))

      val paidResult = commandHandler(twoItemCart, Pay(mastercard))
      paidResult should (
        be (Right(List(Paid(mastercard)))) or
        be (Left(CardProcessFailure(mastercard))))

      val paidCartEither = paidResult.right flatMap { pr =>
        eventHandler(twoItemCart, pr.head)
      }
      paidCartEither should (
        be (Right(PaidCart(List(itemMilk, itemTV), mastercard))) or
        be (Left(CardProcessFailure(mastercard))))

      val wrongCommand = commandHandler(emptyCart, Pay(mastercard))
      wrongCommand shouldBe Left(UnknownCommand(emptyCart, Pay(mastercard)))

      val wrongEvent = eventHandler(emptyCart, LastItemRemoved(itemMilk))
      wrongEvent shouldBe
        Left(UnknownEvent(emptyCart, LastItemRemoved(itemMilk)))
    }
  }
}
