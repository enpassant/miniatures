name := """miniatures"""

version := "1.0"

scalaVersion := "2.12.3"

scalacOptions ++= Seq("-Ypartial-unification", "-feature", "-deprecation")

javaOptions += "-Xmx4512m"

val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.9",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "com.tumblr" %% "colossus" % "0.11.0",
  "org.json4s"             %% "json4s-jackson"        % "3.5.3",
  "com.typesafe.akka"      %% "akka-actor"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-persistence"      % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream"           % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream-kafka"     % "0.17",
  "com.typesafe.akka"      %% "akka-testkit"          % akkaVersion   % "test",
  "com.typesafe.akka"      %% "akka-slf4j"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-http"             % "10.0.6",
  "io.monix"               %% "monix"                 % "2.3.0",
  "org.eclipse.jetty" % "jetty-server" % "9.4.8.v20171121",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.typelevel" %% "cats-free" % "1.1.0",
  "org.typelevel" %% "cats-effect" % "1.0.0-RC",
  "com.github.enpassant" %% "ickenham" % "1.4.1",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1"
)

connectInput in run := true

fork in run := true

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

