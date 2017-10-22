package ddd

import Types._
import PaymentAPI._
import ShoppingCartAPI._

object SampleData {
  val usd = USD(270)
  val gbp = GBP(370)
  val eur = EUR(310)
  val huf = HUF(1)

  val itemMilk = Item("1", "milk", Volume(10), Money(2, usd))
  val itemTV = Item("2", "TV", Volume(7), Money(1000, eur))

  val mastercard = Card(MasterCard, "1234-5678-1234-5678")

  case class WrongCommand(text: String) extends Command

  val convertToCommand = (data: String) => data match {
    case "AddItemMilk" => AddItem(itemMilk)
    case "AddItemTV" => AddItem(itemTV)
    case "RemoveItemMilk" => RemoveItem(itemMilk)
    case "RemoveItemTV" => RemoveItem(itemTV)
    case "PayCard" => Pay(mastercard)
    case _ => WrongCommand(data)
  }
}
