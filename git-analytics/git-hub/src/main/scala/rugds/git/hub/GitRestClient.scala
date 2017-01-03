package rugds.git.hub

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import grizzled.slf4j.Logging
import rugds.service.Service
import spray.json.DefaultJsonProtocol
import rugds.rest.client.{RestClient, RestClientApi}

import scala.util.{Failure, Success}


case class ExampleEntity(value: String)
trait ExampleEntityJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val exampleEntityFormat = jsonFormat1(ExampleEntity)
}

/**
  * = GitRestClient Module =
  */
trait gitRestClient {
    this: Service with RestClient =>

    val gitRestClient = new gitRestClientImpl(restClient)
}

class gitRestClientImpl(restClient: RestClientApi) extends ExampleEntityJsonProtocol with Logging {

    import scala.concurrent.ExecutionContext.Implicits.global

    def getSomeUrl() =
        restClient.get[String]("https://api.github.com/isacandrei").onComplete {
            case Success(response) => info(s"Response: ${response.value}")
            case Failure(t) => error(t)
        }

    def getSomeUrl(url1: Int, url2: Int, url3: Int, param1: String, param2: String) =
        restClient.get[String](s"http://localhost:8000/some/url/$url1/$url2/$url3/withparams?param1=$param1&param2=$param2").onComplete {
            case Success(response) => info(s"Response: ${response.value}")
            case Failure(t) => error(t)
        }

    def postSomeUrlEntity(exampleEntity: ExampleEntity) =
        restClient.post[ExampleEntity, ExampleEntity]("http://localhost:8082/some/url/entity", exampleEntity).onComplete {
            case Success(response) => info(s"Response: ${response.value}")
            case Failure(t) => error(t)
        }
}
