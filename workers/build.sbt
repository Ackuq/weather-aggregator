name := "workers"
scalaVersion := "2.13.6"
version := "0.1.0"
organization := "id2221"


val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.6"


libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "io.spray" %%  "spray-json" % "1.3.6"
)
