name := """miniatures"""

version := "1.0"

scalaVersion := "2.12.8"
//scalaVersion := "2.13.3"
//scalaVersion := "0.26"

scalacOptions ++= Seq("-Ypartial-unification", "-feature", "-deprecation")

javaOptions += "-Xmx4512m"

val akkaVersion = "2.6.10"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "com.storm-enroute" %% "scalameter" % "0.19",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "com.tumblr" %% "colossus" % "0.11.0",
  "org.json4s"             %% "json4s-jackson"        % "3.6.10",
  "com.typesafe.akka"      %% "akka-actor"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-persistence"      % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream"           % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream-kafka"     % "2.0.5",
  "com.typesafe.akka"      %% "akka-testkit"          % akkaVersion   % "test",
  "com.typesafe.akka"      %% "akka-slf4j"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-http"             % "10.2.1",
  "io.monix"               %% "monix"                 % "3.3.0",
  "org.eclipse.jetty" % "jetty-server" % "11.0.0.beta3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.typelevel" %% "cats-core" % "2.2.0",
  "org.typelevel" %% "cats-free" % "2.2.0",
  "org.typelevel" %% "cats-effect" % "3.0.0-f471c52",
  "com.github.enpassant" %% "ickenham" % "1.4.1",
  "org.scalaz" %% "scalaz-zio" % "1.0-RC4",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1",
  "com.h2database" % "h2" % "1.4.200"
)

connectInput in run := true

fork in run := true

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

