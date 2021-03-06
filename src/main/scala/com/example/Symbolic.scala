package com.example

sealed trait Suit { def num: Int }

case object Spade extends Suit { def num = 4 }
case object Heart extends Suit { def num = 3 }
case object Diamond extends Suit { def num = 2 }
case object Club extends Suit { def num = 1 }

object Suit {
  def toString(suit: Suit) = suit match {
    case Spade => "♠"
    case Club => "♣"
    case Heart => "♥"
    case Diamond => "♦"
  }
}

sealed trait CardValue {
  def num: Int

  def suit(suit: Suit) = Card(this, suit)
}

case class CardNum(num: Int) extends CardValue

case object CardAce extends CardValue { def num = 1 }
case object CardKing extends CardValue { def num = 13 }
case object CardQueen extends CardValue { def num = 12 }
case object CardJack extends CardValue { def num = 11 }

object CardValue {
  def toString(cardValue: CardValue) = cardValue match {
    case CardAce => "A"
    case CardKing => "K"
    case CardQueen => "Q"
    case CardJack => "J"
    case _ => cardValue.num.toString
  }
}

case class Card(cardValue: CardValue, suit: Suit) extends Ordered[Card] {
  override def toString =
    (CardValue toString cardValue) + (Suit toString suit)

  import scala.math.Ordered.orderingToOrdered
  def compare(card: Card): Int =
    (suit.num, cardValue.num) compare (card.suit.num, card.cardValue.num)

  def plus(card: Card) = Cards(List(this, card))
}

case class Cards(cards: List[Card]) {
  def plus(card: Card) = copy(cards = cards ++ List(card))
  def and(card: Card) = cards ++ List(card)
}

object Deck {
  type Deck = List[Card]

  val random = new scala.util.Random

  def apply(card: Card*) = List(card :_*)
  def shuffle(deck: Deck) = deck.sortWith { (_, _) => random.nextBoolean }

  def make(card: Card) = Cards(List(card))
}

object Symbolic {
  def main(args: Array[String]): Unit = {
    import Deck._

    val deck1 = Deck(
      Card(CardKing, Spade),
      Card(CardNum(10), Heart),
      Card(CardNum(3), Club),
      Card(CardQueen, Club),
      Card(CardNum(7), Club),
      Card(CardNum(5), Club),
      Card(CardJack, Diamond)
    )

    val deck =
      (CardKing suit Spade) plus
      (CardNum(10) suit Heart) plus
      (CardNum(3) suit Club) plus
      (CardQueen suit Club) plus
      (CardNum(7) suit Club) plus
      (CardNum(5) suit Club) and
      (CardJack suit Diamond)

    println(s"Shuffled deck: ${ Deck shuffle deck }")
    println(s"Sorted deck: ${ deck.sorted }")
    println(s"Face cards: ${ deck filter (_.cardValue.num >= 11) }")
    println(s"King cards: ${ deck filter (_.cardValue == CardKing) }")
    println(s"Simple cards: ${ deck.filter((2 to 10) contains _.cardValue.num) }")
  }
}
