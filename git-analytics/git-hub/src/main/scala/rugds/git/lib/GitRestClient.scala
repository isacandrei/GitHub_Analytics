package rugds.git.hub

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import grizzled.slf4j.Logging
import rugds.service.{Service, ServiceApi, Settings}
import spray.json.DefaultJsonProtocol
import rugds.rest.client.{RestClient, RestClientApi}
import com.typesafe.config.Config
import scala.util.{Failure, Success}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import rugds.auth.{Auth, AuthApi}
import org.json4s.jackson.Serialization.write
import scala.concurrent.Future
import rugds.git.lib._


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
               var no_languages: Int = 1,
               var no_contributors: Int = 1,
               var average_commits: Float = 1
               ){
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

trait GitRestClientSettings extends Settings {
  val org = config.getString("organisation")
}

trait GitRestClientApi extends GitRestClientSettings {
  // def ....

}
/**
  * = GitRestClient Module =
  */
trait gitRestClient {
    this: Service with RestClient with Auth =>

    val gitRestClient = new gitRestClientImpl(service, auth, restClient)

}

class gitRestClientImpl(service: ServiceApi, auth: AuthApi, restClient: RestClientApi) extends GitRestClientApi with ExampleEntityJsonProtocol with Logging {

    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val formats = DefaultFormats
    val token = auth.credentials.getString("accessToken")
  /**
    * = Gets Organisation Repos =
    */
    def getOrganisationRepos() : Future[Seq[Repo]] =
        restClient.get[String](s"https://api.github.com/orgs/$org/repos?access_token=$token").map { response =>
            parse(response.value).extract[Seq[Repo]]
        }

    def getNoLanguages(repo: String) : Future[Int] = {
      restClient.get[String](s"https://api.github.com/repos/$org/$repo/languages?access_token=$token").map( response =>
        parse(response.value).values.toString.split(", ").size
        )
    }

    def getReposDetails(repos : Seq[Repo]) : Unit = Future {
      repos.map(repo => {
        for {
          no_lang <- getNoLanguages(repo.name)
          stats <- getContributorsStats(repo.name)
        } yield {
          repo.no_languages = no_lang
          repo.no_contributors = stats.no
          repo.average_commits = stats.average
          info(repo)
          Tool.writeJson(write(repo),org)
        }
      })
    }

    def getContributorsStats(repo: String) : Future[Tuple]= {
      var s = 0
      var no = 1
      restClient.get[String](s"https://api.github.com/repos/$org/$repo/stats/contributors?access_token=$token").map ( response => {
          val contributions = parse(response.value).extract[Seq[Contribution]].dropWhile(p => p.total < 2).foreach(c => {
            s += c.total
            no += 1
          })
          if (no > 1) { no -= 1 }
          Tuple(s/no, no)
      })
    }


  def GitHubApiMagic() : Unit = {
    for {
      repos <- getOrganisationRepos()
    } yield {
      getReposDetails(repos)
    }

  }

    def postSomeUrlEntity(exampleEntity: ExampleEntity) =
        restClient.post[ExampleEntity, ExampleEntity]("http://localhost:8082/some/url/entity", exampleEntity).onComplete {
            case Success(response) => info(s"Response: ${response.value}")
            case Failure(t) => error(t)
        }

  override def config: Config = service.config
}
