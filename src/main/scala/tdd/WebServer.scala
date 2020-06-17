package tdd

import java.io.{InputStream, OutputStream}
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.ServletException

import java.io.IOException

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ShutdownHandler

object WebServer extends App {
  System.setProperty(
    "org.eclipse.jetty.util.log.class",
    "org.eclipse.jetty.util.log.StdErrLog")
  System.setProperty("org.eclipse.jetty.LEVEL", "OFF")

  val handlers = new HandlerList()
  handlers.setHandlers(
    Array(new ShutdownHandler("password", false, true), JettyHandler))

  val serverJetty = new Server(8001)
  serverJetty.setHandler(handlers)
  serverJetty.start();
  serverJetty.join();

  val server = HttpServer.create(new InetSocketAddress(8000), 0)
  server.createContext("/", RootHandler)
  server.setExecutor(null)

  server.start()

  println("Hit any key to exit...")

  System.in.read()
  server.stop(0)

  object JettyHandler extends AbstractHandler {
    private val processOrderPriceDb =
      OrderPriceHandler.processOrderPrice(Database.getOrder) _

    private[WebServer] def handle(
      target: String,
      baseRequest: Request,
      request: HttpServletRequest,
      resp: HttpServletResponse)
    {
      val result = request match {
        case GetCommandJetty("orders", id, "CalcSumPrice") =>
          processOrderPriceDb(id)
        case _ => None
      }

      val response = result.getOrElse(Response(404, "text/plain", ""))
      processResponse(baseRequest, resp, response)
    }

    private def processResponse(
      request: Request, resp: HttpServletResponse, response: Response) =
    {
      resp.setContentType(response.contentType)
      resp.setStatus(response.code)
      request.setHandled(true)
      resp.getWriter().println(response.content)
    }

    private[tdd] def convertToParameters(query: String): Map[String, String] = {
      val rows = query.split("&")
      rows.map(_.split("=").toList)
        .filter(_.length >= 1)
        .map { keyValue =>
          keyValue match {
            case key :: value :: _ => key -> value
            case key :: Nil => key -> ""
            case _ => "EMPTY" -> ""
          }
        }.toMap
    }
  }

  object RootHandler extends HttpHandler {
    private val processOrderPriceDb =
      OrderPriceHandler.processOrderPrice(Database.getOrder) _

    private[WebServer] def handle(httpExchange: HttpExchange) {
      val result = httpExchange match {
        case GetCommand("orders", id, "CalcSumPrice") =>
          processOrderPriceDb(id)
        case _ => None
      }

      val response = result.getOrElse(Response(404, "text/plain", ""))
      processResponse(httpExchange, response)
    }

    private def processResponse(httpExchange: HttpExchange, response: Response) = {
      val headers = httpExchange.getResponseHeaders
      headers.add("Content-Type", response.contentType)
      httpExchange.sendResponseHeaders(response.code, response.content.length)
      val os = httpExchange.getResponseBody
      os.write(response.content.getBytes)
      os.close()
    }

    private[tdd] def convertToParameters(query: String): Map[String, String] = {
      val rows = query.split("&")
      rows.map(_.split("=").toList)
        .filter(_.length >= 1)
        .map { keyValue =>
          keyValue match {
            case key :: value :: _ => key -> value
            case key :: Nil => key -> ""
            case _ => "EMPTY" -> ""
          }
        }.toMap
    }
  }

  private object GetCommandJetty {
    def unapply(request: HttpServletRequest): Option[(String, String, String)] =
    {
      val method = request.getMethod()
      val query = request.getQueryString()
      val pathItems = request.getPathInfo().split("/")
      val parameters = WebServer.RootHandler.convertToParameters(query)
      if (method == "GET" && pathItems.length > 2 && parameters.contains("cmd")) {
        Some(pathItems(1), pathItems(2), parameters("cmd"))
      } else {
        None
      }
    }
  }

  private object GetCommand {
    def unapply(httpExchange: HttpExchange): Option[(String, String, String)] = {
      val uri = httpExchange.getRequestURI()
      val method = httpExchange.getRequestMethod()
      val pathItems = uri.getPath().split("/")
      val parameters = WebServer.RootHandler.convertToParameters(uri.getQuery())
      if (method == "GET" && pathItems.length > 2 && parameters.contains("cmd")) {
        Some(pathItems(1), pathItems(2), parameters("cmd"))
      } else {
        None
      }
    }
  }
}

object OrderPriceHandler {
  def processOrderPrice(getOrder: String => Option[Order])
    (id: String): Option[Response] =
  {
    for {
      order <- getOrder(id)
      price = calcPrice(order)
    } yield Response(200, "text/plain", price.toString)
  }

  def calcPrice(order: Order): Double = {
    order.rows.map(row => row.price).sum
  }
}
