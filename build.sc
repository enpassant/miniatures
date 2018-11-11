import mill._, scalalib._
import ammonite.ops._

object main extends SbtModule {
  def scalaVersion = "2.12.3"
  override def millSourcePath = super.millSourcePath / up

  def akkaVersion = "2.5.4"
  def scalacOptions = Seq("-Ypartial-unification", "-feature", "-deprecation")

  def ivyDeps = Agg(
    ivy"com.lihaoyi::utest::0.5.4",
    ivy"com.lihaoyi::acyclic:0.1.5",
    ivy"com.storm-enroute::scalameter::0.9",
    ivy"com.chuusai::shapeless::2.3.2",
    ivy"com.tumblr::colossus::0.11.0",
    ivy"org.json4s::json4s-jackson::3.5.3",
    ivy"com.typesafe.akka::akka-actor::${akkaVersion}",
    ivy"com.typesafe.akka::akka-persistence::${akkaVersion}",
    ivy"com.typesafe.akka::akka-stream::${akkaVersion}",
    ivy"com.typesafe.akka::akka-stream-kafka::0.17",
    ivy"com.typesafe.akka::akka-slf4j::${akkaVersion}",
    ivy"com.typesafe.akka::akka-http::10.0.6",
    ivy"io.monix::monix::3.0.0-RC1",
    ivy"org.eclipse.jetty:jetty-server::9.4.8.v20171121",
    ivy"ch.qos.logback:logback-classic::1.2.3",
    ivy"org.typelevel::cats-core::1.1.0",
    ivy"org.typelevel::cats-free::1.1.0",
    ivy"org.typelevel::cats-effect::1.0.0-RC",
    ivy"com.github.enpassant::ickenham::1.4.1",
    ivy"org.scalaz::scalaz-zio::0.1.0-dc8b6a3",
    ivy"com.googlecode.json-simple:json-simple::1.1.1"
  )

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.6.5",
      ivy"com.typesafe.akka::akka-testkit",
      ivy"org.scalatest::scalatest::3.0.4"
    )
    def testFrameworks = Seq("utest.runner.Framework")
  }
}
