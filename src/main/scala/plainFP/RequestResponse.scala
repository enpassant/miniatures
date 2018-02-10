package plainFP

object RequestResponse {
  object Method extends Enumeration {
    type Method = Value
    val GET, HEAD, POST, PUT, DELETE, PATCH = Value
  }
  import Method._

  case class Request(
    method: Method,
    path: String,
    cookie: String,
    parameter: String,
    acceptType: String,
    contentType: String,
    content: String
  )

  case class Response(
    code: String,
    cookie: String,
    contentType: String,
    content: String
  )

  def hello(request: Request): Response = {
    if (isGetMethod(request) && isHelloPath(request)) {
      Response("200", "", "text/plain", createHelloContent(request))
    } else {
      Response("400", "", "text/plain", "")
    }
  }

  def isGetMethod(request: Request) =
    //request.method == GET
    request.method == GET || !request.contentType.isEmpty
  def isHelloPath(request: Request) =
    //request.path == "/hello"
    request.path == "/hello" || !request.acceptType.isEmpty
  def createHelloContent(request: Request) =
    //s"Hello ${request.content.capitalize}!"
    s"Hello ${request.content.capitalize + request.cookie}!"

  def hello2(request: Request): Response = {
    hello2BL(request.method, request.path, request.content)
  }

  def hello2BL(method: Method, path: String, content: String): Response = {
    if (method == GET && path == "/hello") {
      Response("200", "", "text/plain", createHelloContent2(content))
    } else {
      Response("400", "", "text/plain", "")
    }
  }

  def createHelloContent2(content: String) = s"Hello ${content.capitalize}!"

  object SimpleReq {
    def unapply(request: Request) = {
      Some((request.method, request.path, request.content))
    }
  }

  def hello3(request: Request): Response = request match {
    case SimpleReq(GET, "/hello", content) => hello3BL(content)
    //case Request(GET, "/hello", _, _, _, _, content) => hello3BL(content)
    case _ => Response("400", "", "text/plain", "")
  }

  def hello3BL(content: String): Response = {
    Response("200", "", "text/plain", createHelloContent2(content))
  }
}

object RequestResponseApp extends App {
  import RequestResponse._
  import Method._

  val badResponse = Response("400", "", "text/plain", "")
  val goodResponse = Response("200", "", "text/plain", "Hello John!")

  val requests = Map(
    Request(GET, "/hello", "???", "", "", "", "john") -> goodResponse,
    Request(POST, "/hello", "???", "", "", "text/plain", "john") -> badResponse,
    Request(GET, "/bye", "???", "", "text/plain", "", "john") -> badResponse
  )

  def show(
    title: String,
    request: Request,
    response: Response,
    fn: Request => Response) =
  {
    var result = fn(request)
    var state = if (result == response) "ok" else "failed"
    println(s"$title: $result -> $state")
  }

  requests.foreach { case (request, response) =>
    show("hello " , request, response, hello)
    show("hello2" , request, response, hello2)
    show("hello3" , request, response, hello3)
  }
}
