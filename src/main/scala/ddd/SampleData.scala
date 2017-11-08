package ddd

import Types._
import PaymentAPI._
import ShoppingCartAPI._

class SampleData {
  val usd = USD(270)
  val gbp = GBP(370)
  val eur = EUR(310)
  val huf = HUF(1)

  //val itemMilk = Item("1", String80("milk"), Volume(10), Money(2, usd))
  val itemTV = Item("2", "TV", Volume(7), Money(1000, eur))

  def checkLength(name: String): Validation[String] = {
    if (name.length <= 2) Validation(name)
    else Validation(name, TooLong(name))
  }

  def checkMax(value: BigDecimal, max: BigDecimal): Validation[BigDecimal] = {
    if (value <= max) Validation(value)
    else Validation(value, TooBig(value))
  }

  val itemMilkChecked: Validation[Item] = (for {
    name <- checkLength("milk")
    volume <- checkMax(10000, 1000)
  } yield Item("1", name, Volume(volume), Money(2, usd))).noError

  println(s"itemMilkChecked: $itemMilkChecked")

  val itemMilk = itemMilkChecked getOrElse
    Item("1", "milk", Volume(10), Money(2, usd))

  val mastercard = Card(MasterCard, "1234-5678-1234-5678")

  val convertToCommand = (data: String) => data match {
    case "AddItemMilk" => AddItem(itemMilk)
    case "AddItemTV" => AddItem(itemTV)
    case "PayCard" => Pay(mastercard)
    case "PayCash" => Pay(Cash)
    case "" => NoCommand
    case _ => WrongCommand(data)
  }
}

case class WrongCommand(text: String) extends Command

object SampleData {
  val sd: Validation[SampleData] = Passed(new SampleData)
  val convertToCommand = (data: String) => sd match {
    case Passed(sd, _) => sd.convertToCommand(data)
    case _ => WrongCommand(data)
  }
}
