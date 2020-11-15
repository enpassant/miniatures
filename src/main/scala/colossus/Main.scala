package colossus
// Remove because Colossus is not actively developed.
/*
import colossus.core._
import colossus.core.server.Server
import colossus.controller._
import colossus.protocols.http._
import colossus.protocols.http.filters.HttpCustomFilters.CompressionFilter
import colossus.protocols.websocket._
import UrlParsing._
import HttpMethod._
import colossus.service.Callback.Implicits._
import colossus.service.Callback
import colossus.service.GenRequestHandler.PartialHandler
import subprotocols.rawstring._
import akka.actor.ActorSystem

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import scala.io.Source

import com.github.enpassant.ickenham._
import com.github.enpassant.ickenham.adapter.PlainAdapter

class HelloHandler(context: ServerContext) extends RequestHandler(context) {
  implicit object JsonBody extends HttpBodyEncoder[JValue] {
    val contentType = ContentType.ApplicationJson
    def encode(json: JValue)  = {
      HttpBody(compact(render(json)))
    }
  }

  val json : JValue   = ("message" -> "Hello, World!")

  def handle = {
    case request @ Get on Root / "hello" => {
      Callback.successful(request.ok("Hello World!"))
    }
    case request @ Get on Root / "json" => {
      Callback.successful(request.ok(json))
    }
  }
}

class HelloInitializer(init: InitContext) extends Initializer(init) {
  println(init)
  def onConnect = context => {
    println(context)
    new HelloHandler(context)
  }
}


object Main extends App {
  implicit val actorSystem = ActorSystem()
  implicit val io = IOSystem()

  def start[E <: Encoding](
    name: String,
    port: Int,
    upgradePath: String = "/websocket",
    origins: List[String] = List.empty,
    httpHandler: PartialFunction[HttpRequest, Callback[HttpResponse]] =
      PartialFunction.empty)
  (init: WorkerRef => WebsocketInitializer[E])
  (implicit io: IOSystem) =
  {
    HttpServer.start(name, port){context => new Initializer(context) {
      val websockinit : WebsocketInitializer[E] = init(context.worker)
      def onConnect =
        new WebsocketHttpHandler(_, websockinit, upgradePath, origins)
      {
        override def handle = super.handle orElse httpHandler
      }
    }}
  }

  implicit object JsonBody extends HttpBodyEncoder[JValue] {
    val contentType = ContentType.ApplicationJson
    def encode(json: JValue)  = {
      HttpBody(compact(render(json)))
    }
  }

  val json : JValue   = ("message" -> "Hello, World!")

  case object AcceptUrlOn {
    def unapply(request: HttpRequest): Option[(HttpMethod, String, String)] = {
      val component = request.head.url match {
        case ""          => "/"
        case url: String => url
      }
      request.head.headers.firstValue(HttpHeaders.Accept) map { accept =>
        (request.head.method, component, accept)
      }
    }
  }

  case object AcceptOn {
    def unapply(request: HttpRequest): Option[(HttpMethod, String)] = {
      request.head.headers.firstValue(HttpHeaders.Accept) map { accept =>
        (request.head.method, accept)
      }
    }
  }

  val serverHeader = HttpHeader("Server", "Colossus")
  val htmlHeader =
    HttpHeader(HttpHeaders.ContentType, "text/html")
  val plainTextHeader =
    HttpHeader(HttpHeaders.ContentType, ContentType.TextPlain)
  val jsonHeader =
    HttpHeader(HttpHeaders.ContentType, ContentType.ApplicationJson)
  val templateHeader =
    HttpHeader(HttpHeaders.ContentType, "text/html+xml")

  val dateHeader = new DateHeader
  val headers = HttpHeaders(serverHeader, dateHeader)

  val fromResource: String => String = resource =>
    Source.fromResource(resource).getLines.reduce(_ + _)

  def completePageC(render: Object => String, template: String)
    (makeObject: => Option[JValue]): PartialHandler[Http] =
  {
    val resource = fromResource(template + ".hbs");

    {
      case request @ AcceptOn(Get, "text/html") =>
        makeObject match {
          case Some(obj) =>
            request.ok(render(obj), headers + htmlHeader)
          case _ =>
            request.notFound("")
        }
      case request @ AcceptOn(Get, "application/json") =>
        makeObject match {
          case Some(obj) =>
            request.ok(obj, headers + jsonHeader)
          case _ =>
            request.notFound("")
        }
      case request @ AcceptOn(Get, "text/html+xml") =>
        request.ok(resource, headers + templateHeader)
    }
  }

  val index = fromResource("index.html")
  val handleBlogs = completePageC(o => s"Obj: $o", "comment") {
    None
  }

  val discussionPlain = Map(
    "_id" -> 5,
    "escape" -> "5 < 6",
    "comments" -> List(
      Map(
        "commentId" -> "7",
        "userName" -> "John",
        "content" -> "<h1>Test comment 1</h1>",
        "comments" -> List(
          Map(
            "commentId" -> "8",
            "userName" -> "Susan",
            "content" -> "<h2>Reply</h2>"
          )
        )
      ),
      Map(
        "commentId" -> "9",
        "userName" -> "George",
        "content" -> "<h1>Test comment 2</h1>"
      )
    )
  )

  val ickenham = new Ickenham(new PlainAdapter())
  val assembledFn = ickenham.compile("comment")

  val handleWithIckenham = {
    json: Any =>
      assembledFn(List(json))
  }

  val server: ServerRef = start("hello-world", 9000, httpHandler = new CompressionFilter()({
    case request @ AcceptUrlOn(Get, "/hello", ContentType.TextPlain) => {
      request.ok("Hello World!", headers + plainTextHeader)
    }
    case request @ Get on Root / "json" => {
      request.ok(json, headers + jsonHeader)
    }
    case request @ Get on Root / "file" => {
      handleBlogs(request)
    }
    case request @ Get on Root / "ickenham" => {
      request.ok(handleWithIckenham(discussionPlain), headers + htmlHeader)
    }
    case request @ Get on Root / "shutdown" => {
      Main.server.shutdown
      request.ok("Shutting down", headers + plainTextHeader)
    }

  })) { worker => new WebsocketInitializer[RawString](worker) {
      def provideCodec() = new RawStringCodec

      def onConnect =
        ctx => new WebsocketServerHandler[RawString](ctx) with ProxyActor
      {
        def shutdownRequest() {
          upstream.connection.disconnect()
        }

        override def onConnected() {
          send("HELLO THERE!")
        }

        override def onShutdown() {
          send("goodbye!")
        }

        def handle = {
          case "EXIT" => {
            upstream.connection.disconnect()
          }
          case other => {
            send(s"unknown command: $other")
          }
        }

        def handleError(reason: Throwable){}

        def receive = {
          case _ =>
        }
}  }}
}
*/
