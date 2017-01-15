name := """miniatures"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.7",
  "com.chuusai" %% "shapeless" % "2.2.5"
)

connectInput in run := true

fork in run := true

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

