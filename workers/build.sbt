name := "workers"
scalaVersion := "2.13.6"
version := "0.1.0"
organization := "id2221"

fork in run := true

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "io.spray" %% "spray-json" % "1.3.6",
  "org.apache.kafka" %% "kafka" % "2.8.1",
  "id2221" %% "scala-common" % "0.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
)
