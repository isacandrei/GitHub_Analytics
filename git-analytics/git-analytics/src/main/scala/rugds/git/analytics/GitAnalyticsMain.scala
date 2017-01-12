package rugds.git.analytics

import java.io.File

import com.typesafe.config.ConfigFactory
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization.write
import rugds.git.hub.{Repo, Tool, Tuple, gitRestClient}
import rugds.rest.client.RestClient
import rugds.service.CoreService
import rugds.spark.app.sparkAppClient

import scala.collection.mutable.ListBuffer

object GitAnalyticsMain extends App{

  val service = new CoreService with RestClient with gitRestClient with sparkAppClient{

    implicit val formats = DefaultFormats

    /**
      * The ExampleRestClient module provides an easy way to call the URLs
      * made available by the ExampleRestRoute module.
      */


    val path = new File(".").getCanonicalPath

    val config = ConfigFactory.parseFile(new File(path + "/git-hub/src/main/resources/application.conf"))

    val token = config.getString("gitHub.accessToken")

    val org = config.getString("organisation")

    gitRestClient.getOrganisationRepos(token, org)
    val projects = parse(Tool.readJson(org)).extract[Seq[Repo]]
    var s = 0
    var projectsFinal = new ListBuffer[Repo]()
    projects.foreach(project => {
      s = 0
      gitRestClient.getContributorsStats(token, org, project.name)
      gitRestClient.getNoLanguages(token, org, project.name)
    })
    projects.foreach(project => {
      val stats =       parse(Tool.readJson(org + project.name)).extract[Tuple]
      val noLanguages = parse(Tool.readJson(org + project.name + "language")).extract[Int]
      projectsFinal += new Repo(project, noLanguages, stats.no, stats.average)
    })
    println(projectsFinal)

    Tool.writeJson(write(projectsFinal), org)

    sparkAppClient.sparkMagic()

  }

}
