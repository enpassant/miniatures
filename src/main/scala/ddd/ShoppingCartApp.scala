package ddd

import ErrorAPI._
import Types._
import ShoppingCartAPI._
import ShoppingCart._
import ShoppingCartConfig._
import PaymentAPI._
import SampleData._
import Directives._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, Link, LinkParams, LinkValue}
import akka.stream.ActorMaterializer
import java.util.UUID
import scala.io.StdIn

object ShoppingCartApp extends App {
    implicit val system = ActorSystem("ShoppingCart")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    var appState = setState(EmptyCart)

    val route =
      path("") {
        respondCapabilities(appState.uris) {
          headComplete
        }
      } ~
      path(Segment) { uri =>
        post {
          entity(as[String]) { data =>
            if (appState.uris contains uri) {
              val command = convertToCommand(data)
              val rel = appState.uris(uri)
              val capability = appState.capabilities(rel) orElse wrongCapability
              val stateResultEither = capability(appState.state, command)
              stateResultEither.right foreach { stateResult =>
                appState = setState(stateResult.state)
              }
              respondCapabilities(appState.uris) {
                complete(stateResultEither.toString)
              }
            } else reject
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

    println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
