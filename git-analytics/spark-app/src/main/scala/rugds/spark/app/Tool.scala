package rugds.spark.app

import java.io.{BufferedWriter, File, FileWriter}

/**
  * Created by isac on 05/01/2017.
  */
object Tool {

  def writeJson (json: String, org: String): Unit = {
    val path = new File(".").getCanonicalPath
    val file = new File(s"$path/$org.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(json)
    bw.close()
  }

  def readJson (org: String): String = {
    val path = new File(".").getCanonicalPath
    val source = scala.io.Source.fromFile(s"$path/$org.txt")
    val lines = try source.mkString finally source.close()
    lines
  }


//  def readGrades (org: String): String = {
//
//  }
}
