package colossus

import colossus._
import core._
import controller._
import service._
import protocols.http._
import colossus.protocols.http.server._
import colossus.protocols.websocket._
import UrlParsing._
import HttpMethod._
import colossus.service.Callback.Implicits._
import colossus.service.Callback
import subprotocols.rawstring._
import akka.actor.ActorSystem

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

class HelloHandler(context: ServerContext) extends RequestHandler(context) {
  implicit object JsonBody extends HttpBodyEncoder[JValue] {
    def encode(json: JValue)  = {
      HttpBody(compact(render(json))).withContentType("application/json")
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
      def onConnect = new WebsocketHttpHandler(_, websockinit, upgradePath, origins) {
        override def handle = super.handle orElse httpHandler
      }
    }}
  }

  implicit object JsonBody extends HttpBodyEncoder[JValue] {
    def encode(json: JValue)  = {
      HttpBody(compact(render(json))).withContentType("application/json")
    }
  }

  val json : JValue   = ("message" -> "Hello, World!")

  start("hello-world", 9000, httpHandler = {
    case request @ Get on Root / "hello" => {
      Callback.successful(request.ok("Hello World!"))
    }
    case request @ Get on Root / "json" => {
      Callback.successful(request.ok(json))
    }

  }) { worker => new WebsocketInitializer[RawString](worker) {
      def provideCodec() = new RawStringCodec

      def onConnect = ctx => new WebsocketServerHandler[RawString](ctx) with ProxyActor {

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
