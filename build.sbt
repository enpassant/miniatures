name := """miniatures"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.chuusai" %% "shapeless" % "2.2.5"
)

connectInput in run := true

fork in run := true
