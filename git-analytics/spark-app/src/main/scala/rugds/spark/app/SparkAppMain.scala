package rugds.spark.app

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import rugds.service.CoreService

object SparkAppMain extends App{

  val service = new CoreService with sparkAppClient {
    sparkAppClient.sparkMagic()

  }

  //  def main(args: Array[String]) {
  //    println("Hello World!!!")
  //
  //    val logFile = "/Users/lex/Programs/spark-2.0.2-bin-hadoop2.7/README.md" // Should be some file on your system
  //    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[1]")
  //    val sc = new SparkContext(conf)
  //    val logData = sc.textFile(logFile, 2).cache()
  //    val numAs = logData.filter(line => line.contains("a")).count()
  //    val numBs = logData.filter(line => line.contains("b")).count()
  //    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  //
  //  }

}
