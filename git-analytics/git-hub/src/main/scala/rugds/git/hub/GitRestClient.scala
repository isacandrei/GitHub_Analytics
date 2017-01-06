package rugds.git.hub

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import grizzled.slf4j.Logging
import rugds.service.Service
import spray.json.DefaultJsonProtocol
import rugds.rest.client.{RestClient, RestClientApi}
import spray.json._
import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)
import scala.util.{Failure, Success}

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write

case class ExampleEntity(value: String)
trait ExampleEntityJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val exampleEntityFormat = jsonFormat1(ExampleEntity)
}

case class Repo(
               name: String,
               size: Int,
               language: String,
               forks_count: Int,
               open_issues: Int,
               has_issues: Boolean,
               no_languages: Int,
               no_contribuitors: Int,
               average_commits: Float
               ){
  def this(name: String, size: Int, language: String, forks_count: Int, open_issues: Int, has_issues: Boolean) = this(name, size, language, forks_count, open_issues, has_issues,1 ,1 ,1)
  def this(repo: Repo, l: Int, c: Int, a: Float) = this(repo.name, repo.size, repo.language, repo.forks_count, repo.open_issues, repo.has_issues, l, c, a)
}

case class Language(
                   name: String,
                   size: Int
                   )

case class Contribution(
                       total: Int,
                       author: Author
                       )

case class Author(login: String)

case class Tuple (
                   var average: Float,
                   var no: Int
                 )
/**
  * = GitRestClient Module =
  */
trait gitRestClient {
    this: Service with RestClient =>

    val gitRestClient = new gitRestClientImpl(restClient)
}

class gitRestClientImpl(restClient: RestClientApi) extends ExampleEntityJsonProtocol with Logging {

    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val formats = DefaultFormats
    /**
    * = Gets Organisation Repos =
    */
    def getOrganisationRepos(token: String, org: String) =
        restClient.get[String](s"https://api.github.com/orgs/$org/repos?access_token=$token").onComplete {
            case Success(response) => Tool.writeJson(write(parse(response.value).extract[Seq[Repo]]),org)
            case Failure(t) => error(t)
        }

    def getNoLanguages(token: String, org: String, repo: String) = {
      var i = 1
      restClient.get[String](s"https://api.github.com/repos/$org/$repo/languages?access_token=$token").onComplete {
            case Success(response) => Tool.writeJson(parse(response.value).values.toString.split(", ").size.toString, org + repo + "language")
        }
      i
    }

    def getContributorsStats(token: String, org: String, repo: String) = {
      var s = 0
      var no = 1
      restClient.get[String](s"https://api.github.com/repos/$org/$repo/stats/contributors?access_token=$token").onComplete {
        case Success(response) =>
          val contributions = parse(response.value).extract[Seq[Contribution]].dropWhile(p => p.total < 2).foreach(c => {
            s += c.total
            no += 1
          })
          var t = new Tuple(s/no, no)
          Tool.writeJson(write(t), org + repo)
      }
    }


    def postSomeUrlEntity(exampleEntity: ExampleEntity) =
        restClient.post[ExampleEntity, ExampleEntity]("http://localhost:8082/some/url/entity", exampleEntity).onComplete {
            case Success(response) => info(s"Response: ${response.value}")
            case Failure(t) => error(t)
        }

}
