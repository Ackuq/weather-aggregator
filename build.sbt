name := "weather-aggregator"
version := "0.1.0"
organization := "id2221"

lazy val `scala-common` =
  project.in(file("scala-common")).withId("scala-common")
lazy val workers = project.in(file("workers")).dependsOn(`scala-common`)
lazy val `http-proxy` =
  project.in(file("http-proxy")).dependsOn(`scala-common`).withId("http-proxy")
lazy val spark = project.in(file("spark")).dependsOn(`scala-common`)
