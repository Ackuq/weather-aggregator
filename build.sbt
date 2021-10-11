name := "weather-aggregator"
version := "0.1.0"
organization := "id2221"

lazy val scalaCommon = project.in(file("scala-common"))
lazy val `scala-common` = scalaCommon.withId("scala-common")
lazy val workers = project.in(file("workers")).dependsOn(`scala-common`)
lazy val httpProxy = project.in(file("http-proxy")).withId("http-proxy")
lazy val `http-proxy` = httpProxy.withId("http-proxy").dependsOn(`scala-common`)
lazy val spark = project.in(file("spark")).dependsOn(`scala-common`)
