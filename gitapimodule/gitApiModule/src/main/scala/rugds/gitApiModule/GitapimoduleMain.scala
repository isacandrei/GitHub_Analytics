package rugds.gitApiModule

import rugds.gitApiModule.{gitRestClient}
import rugds.rest.client.RestClient
import rugds.rest.server.RestServer
import rugds.service.CoreService

object GitapimoduleMain extends App {
  val service = new CoreService with RestClient with gitRestClient {

    /**
      * The ExampleRestClient module provides an easy way to call the URLs
      * made available by the ExampleRestRoute module.
      */
    gitRestClient.getSomeUrl() // GET ../some/url
  }
}