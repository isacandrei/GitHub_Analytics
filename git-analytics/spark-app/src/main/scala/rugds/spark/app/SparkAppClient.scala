package rugds.spark.app

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import rugds.service.Service
import org.json4s.jackson.Serialization.write
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import rugds.git.lib._

/**
  * Created by lex
  */

case class Repo(
                 name: String,
                 size: Int,
                 language: String,
                 forks_count: Int,
                 open_issues: Int,
                 has_issues: Boolean,
                 no_languages: Int,
                 no_contributors: Int,
                 average_commits: Float
               )

trait sparkAppClient {
  this: Service =>

  val sparkAppClient = new sparkAppClientImpl()
}

class sparkAppClientImpl {

  implicit val formats = DefaultFormats

  def parseMyRepo(repo: Repo) = {
    var lang = 0
    if (repo.language=="Scala") {
      lang = 1
    }
    val arr = Array(repo.size, lang, repo.open_issues, repo.no_languages, repo.no_contributors, repo.average_commits)
    arr
  }

  def sparkMagic(org: String) = {
    val conf = new SparkConf().setAppName("LinearRegressionWithSGDExample").setMaster("local[*]")
    val sc = new SparkContext(conf)

    var repos = new ListBuffer[Repo]()
    // Load and parse the data
    Tool.readJson(org).split("}").foreach(json =>{
      repos += parse(json + "}").extract[Repo]
    })

    val lines = Tool.readJson("rug-wacc-grades").split("\n")
    val grades = mutable.Map[String, Float]()
    lines.foreach(line => {
      val g = line.split(' ')
      grades.update(g(0).toString, g(1).toFloat)
    })

    val parsedData = repos.map{ r =>
      LabeledPoint(grades(r.name).toDouble, Vectors.dense(parseMyRepo(r).map(_.toDouble)))
    }

    // Building the model
    val numIterations = 100
    val stepSize = 0.000000001
    val model = LinearRegressionWithSGD.train(sc.parallelize(parsedData), numIterations, stepSize)

    // Evaluate model on training examples and compute training error
    val result = repos.map{ r =>
      val prediction = model.predict(Vectors.dense(parseMyRepo(r).map(_.toDouble)))
      (r.name, prediction)
    }

    Tool.writeJson(write(result),org + "-results")
    sc.stop()
  }
}