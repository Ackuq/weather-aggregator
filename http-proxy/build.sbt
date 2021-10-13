ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "id2221"
ThisBuild / version := "1.0.0"

lazy val `http-proxy` = (project in file("."))
  .settings(
    name := "http-proxy"
  )
  .enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  caffeine,
  "org.apache.kafka" %% "kafka" % "2.8.1",
  "id2221" %% "scala-common" % "0.1.0"
)
