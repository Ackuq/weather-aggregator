package id2221;

import org.apache.spark.sql.SparkSession;
import id2221.common.ForecastParser;
import id2221.tests.{ProducerTest};
import id2221.spark.SparkConsumer;

object Main {
  def main(args: Array[String]) {
    
    // Start the spark consumer in a new thread (Currently to allow to also start a producer test that produces fake forecast for testing)
    new Thread(new SparkConsumer()).start();
    // println("Starting Producer test")
    // new Thread(new ProducerTest()).start();
  }
}
