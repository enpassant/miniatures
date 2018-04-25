package tdd

import scala.util.Random

object Method extends Enumeration {
  type Method = Value
  val GET, HEAD, POST, PUT, DELETE, PATCH = Value
}
import Method._

case class Response(
  code: Int,
  contentType: String,
  content: String
)

case class Order(id: String, rows: List[OrderRow])

case class OrderRow(id: String, price: Double)

object Database {
  def getOrder(id: String): Option[Order] = {
    //Thread.sleep(2000)
    if (id == "15") {
      Some(Order(id, List(OrderRow("1", 10000.0), OrderRow("2", 17456.0))))
    } else if (Random.nextBoolean) {
      Some(Order(id, List(
        OrderRow("1", Random.nextDouble),
        OrderRow("2", Random.nextDouble))))
    } else None
  }
}
