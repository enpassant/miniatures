package ddd

import ErrorAPI._
import Types._
import ShoppingCartAPI._
import ShoppingCart._

object ShoppingCartConfig {
  val wrongCommand: CommandHandler[State, Command, Event] = {
    case (state: State, command: Command) =>
      Left(UnknownCommand(state, command))
  }

  val commandHandler =
    addToEmpty orElse
    addToActive orElse
    removeFromActive orElse
    payActive orElse
    wrongCommand

  val wrongEvent: EventHandler[State, Event, State] = {
    case (state: State, event: Event) =>
      Left(UnknownEvent(state, event))
  }

  val eventHandler =
    handleFirstItemAdded orElse
    handleNextItemAdded orElse
    handleAnItemRemoved orElse
    handleLastItemRemoved orElse
    handlePaid orElse
    wrongEvent

  val getCapabilities =
    getEmptyCartCaps orElse
    getActiveCartCaps orElse
    getPaidCartCaps

  val wrongCapability = createCapability(wrongCommand, wrongEvent)
}
