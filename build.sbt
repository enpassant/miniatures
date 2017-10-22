name := """miniatures"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-deprecation")

javaOptions += "-Xmx512m"

val akkaVersion = "2.5.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.7",
  "com.chuusai" %% "shapeless" % "2.2.5",
  "com.tumblr" %% "colossus" % "0.9.0",
  "org.json4s"             %% "json4s-jackson"        % "3.3.0",
  "com.typesafe.akka"      %% "akka-actor"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-persistence"      % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream"           % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream-kafka"     % "0.13",
  "com.typesafe.akka"      %% "akka-testkit"          % akkaVersion   % "test",
  "com.typesafe.akka"      %% "akka-slf4j"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-http"             % "10.0.6",
  "org.typelevel" % "cats-core_2.11" % "0.4.1"
)

connectInput in run := true

fork in run := true

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

