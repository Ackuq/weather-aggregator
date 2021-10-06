name := """http-proxy"""
organization := "id2221"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  guice,
  "org.apache.kafka" % "kafka_2.11" % "1.0.0"
)