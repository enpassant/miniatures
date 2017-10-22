package ddd

import Types._
import ShoppingCartConfig._
import SampleData._

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, Link, LinkParams, LinkValue}
import java.util.UUID

case class AppState(
  state: State,
  capabilities: Map[String, Capability],
  uris: Map[String, String]
)

object Directives {
    def setState(newState: State) = {
      val capabilities = getCapabilities(newState)
      val uris = (capabilities.keys map { key =>
        (UUID.randomUUID.toString -> key)
      }).toMap
      AppState(newState, capabilities, uris)
    }

    def headComplete = (get | options | head) { complete("") }

    def stringLink(uri: String, rel: String, methods: HttpMethod*) = {
        LinkValue(Uri(uri),
            LinkParams.rel(rel),
            LinkParams.`type`(MediaTypes.`text/plain`.withParams(
                Map("method" -> methods.map(_.name).mkString(" ")))))
    }

    def respondCapabilities(uris: Map[String, String]) = {
        val links = (uris.keys map {
          uri => stringLink(uri, uris(uri), POST)
        }).toList

        respondWithLinks(links:_*)
    }

    def respondWithLinks(links: LinkValue*) =
      respondWithHeader(Link(links : _*))
}
