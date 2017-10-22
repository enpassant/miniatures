package ddd

import Types._

object PaymentAPI {
  sealed trait CardType
  case object MasterCard extends CardType
  case object Visa extends CardType

  type CardNumber = String

  sealed trait Currency {
    def rate: BigDecimal
    def sign: String
  }
  case class USD(rate: BigDecimal, sign: String = "$") extends Currency
  case class GBP(rate: BigDecimal, sign: String = "£") extends Currency
  case class EUR(rate: BigDecimal, sign: String = "€") extends Currency
  case class HUF(rate: BigDecimal, sign: String = "Ft") extends Currency
  case class Money(amount: BigDecimal, currency: Currency)

  sealed trait Payment
  case object Cash extends Payment
  case class Cheque(money: Money) extends Payment
  case class Card(cardType: CardType, cardNumber: CardNumber) extends Payment
}
