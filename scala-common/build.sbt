// Support both 2.12 and 2.13
lazy val supportedScalaVersions = List("2.13.6", "2.12.15")

ThisBuild / version := "0.1.0"
ThisBuild / organization := "id2221"
ThisBuild / organizationName := "id2221"
ThisBuild / crossScalaVersions := supportedScalaVersions

lazy val `scala-common` = (project in file("."))
  .settings(
    name := "scala-common"
  )
