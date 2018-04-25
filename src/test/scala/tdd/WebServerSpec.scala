package tdd

import org.scalatest._

import scala.util.Random

class WebServerSpec extends FunSpec with Matchers {
  val orderId = "15"
  val testOrder =
    Order(orderId, List(OrderRow("1", 10000.0), OrderRow("2", 17456.0)))

  def getOrder(id: String): Option[Order] = {
    if (id == orderId) Some(testOrder) else None
  }

  describe("WebServer.processOrderPrice") {
    it("should respond sum price of order for valid order") {
      val response = OrderPriceHandler.processOrderPrice(getOrder)("15")
      response shouldBe Some(Response(200, "text/plain", "27456.0"))
    }
  }

  describe("WebServer.processOrderPrice") {
    it("should respond None for invalid order") {
      val response = OrderPriceHandler.processOrderPrice(getOrder)("10")
      response shouldBe None
    }
  }

  describe("WebServer.calcPrice") {
    it("should calc sum price of order") {
      val price = OrderPriceHandler.calcPrice(testOrder)
      price shouldBe 27456.0
    }
  }

  describe("WebServer.convertToParameters") {
    it("should convert to map of parameters") {
      val parameters = WebServer.RootHandler.convertToParameters(
        "cmd=CalcSumPrice&discount=10")
      parameters shouldBe Map("cmd" -> "CalcSumPrice", "discount" -> "10")
    }
  }
}
