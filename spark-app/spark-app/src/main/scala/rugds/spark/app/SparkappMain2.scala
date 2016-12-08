package rugds.spark.app

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

object SparkappMain2 {

    def main(args: Array[String]): Unit = {
      val conf = new SparkConf().setAppName("LinearRegressionWithSGDExample").setMaster("local[1]")
      val sc = new SparkContext(conf)

      // $example on$
      // Load and parse the data
      val data = sc.textFile("spark-app/src/data/test.txt")
      println("test")
      val parsedData = data.map { line =>
        val parts = line.split(',')
        LabeledPoint(parts(1).toDouble, Vectors.dense(parts(0).split(' ').map(_.toDouble)))
      }.cache()

      for (point <- parsedData) {
        println(point.label)
      }

      println(parsedData.toString)

      // Building the model
      val numIterations = 100
      val stepSize = 1
      val model = LinearRegressionWithSGD.train(parsedData, numIterations, stepSize)

      // Evaluate model on training examples and compute training error
      val valuesAndPreds = parsedData.map { point =>
        val prediction = model.predict(point.features)
        println(point.label + " " + prediction)
        (point.label, prediction)
      }
      val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
      println("training Mean Squared Error = " + MSE)

      // Save and load model
      model.save(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
      val sameModel = LinearRegressionModel.load(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
      // $example off$

      sc.stop()
    }
  }

