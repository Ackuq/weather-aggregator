package id2221;

import org.apache.spark.sql.SparkSession;

object Main {
  def main(args: Array[String]) {
    val spark = SparkSession.builder
      .appName("SparkApplication")
      .config("spark.master", "local")
      .getOrCreate;

    spark.stop()
  }
}
