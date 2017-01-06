package rugds.git.hub

import java.io.{File}

import com.typesafe.config.ConfigFactory
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import rugds.rest.client.RestClient
import rugds.service.CoreService
import org.json4s.jackson.Serialization.write

import scala.collection.mutable.ListBuffer


object GitHubMain extends App {
  val service = new CoreService with RestClient with gitRestClient {

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
      val stats =       parse(Tool.readJson(org + project.name)).extract[Tuple]
        gitRestClient.getNoLanguages(token, org, project.name)
      val noLanguages = parse(Tool.readJson(org + project.name + "language")).extract[Int]

        projectsFinal += new Repo(project, noLanguages, stats.no, stats.average)
    })
    println(projectsFinal)

    Tool.writeJson(write(projectsFinal), org)
  }
}