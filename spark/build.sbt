name := "spark"

version := "0.0.1"

scalaVersion := "2.12.15"

val sparkVersion = "3.1.1"
val hadoopVersion = "3.2.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
  "id2221" %% "scala-common" % "0.1.0"
)
