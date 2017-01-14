package rugds.git.lib

import java.io.{File, FileWriter}

import scala.io.Source

/**
  * Created by isac on 05/01/2017.
  */
object Tool {

  def getPath(org:String): String = {
    val path = new File(".").getCanonicalPath
    s"$path/$org.txt"
  }

  def getFileWriter(org: String, append: Boolean = true) : FileWriter = {
    new FileWriter(new File(getPath(org)), append)
  }

  def writeJson (json: String, org: String): Unit = {
    val fileWriter = getFileWriter(org)
    fileWriter.write(json)
    fileWriter.close()
  }

  def readJson (org: String): String = {
    val source = Source.fromFile(getPath(org))
    val lines = try source.mkString finally source.close()
    lines
  }

}