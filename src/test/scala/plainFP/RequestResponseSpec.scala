package plainFP

import org.scalatest._
import org.scalatest.Matchers._

class RequestResponseSpec extends FunSpec with Matchers {
  import RequestResponse._

  val badResponse = Response("400", "", "text/plain", "")
  val goodResponse = Response("200", "", "text/plain", "Hello John!")

  describe("hello") {
    it("should give good response") {
      val request = Request("GET", "/hello", "", "", "", "", "john")
      val response = hello(request)
      response shouldEqual goodResponse
    }
    it("should give bad response if method is wrong") {
      val request = Request("POST", "/hello", "???", "", "", "", "john")
      val response = hello(request)
      response shouldEqual badResponse
    }
    it("should give bad response if path is wrong") {
      val request = Request("GET", "/bye", "???", "", "", "", "john")
      val response = hello(request)
      response shouldEqual badResponse
    }
  }

  describe("isMethod") {
    it("should give true if method is equal") {
      val request = Request("GET", "", "", "", "", "", "")
      val response = isGetMethod(request)
      response shouldEqual true
    }
    it("should give false if method is wrong") {
      val request = Request("POST", "", "", "", "", "", "")
      val response = isGetMethod(request)
      response shouldEqual false
    }
  }

  describe("isPath") {
    it("should give true if path is equal") {
      val request = Request("", "/hello", "", "", "", "", "")
      val response = isHelloPath(request)
      response shouldEqual true
    }
    it("should give false if path is wrong") {
      val request = Request("", "/bye", "", "", "", "", "")
      val response = isHelloPath(request)
      response shouldEqual false
    }
  }

  describe("createHelloContent") {
    it("should give 'Hello John!'") {
      val request = Request("", "", "", "", "", "", "john")
      val response = createHelloContent(request)
      response shouldEqual "Hello John!"
    }
    it("should give 'Hello !' if name is empty") {
      val request = Request("", "", "", "", "", "", "")
      val response = createHelloContent(request)
      response shouldEqual "Hello !"
    }
  }
}
