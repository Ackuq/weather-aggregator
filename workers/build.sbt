name := "workers"
scalaVersion := "2.13.6"
version := "0.1.0"
organization := "id2221"

val AkkaVersion = "10.2.6"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
)
