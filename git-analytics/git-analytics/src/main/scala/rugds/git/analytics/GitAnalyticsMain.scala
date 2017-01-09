package rugds.git.analytics

import rugds.git.hub.gitRestClient
import rugds.rest.client.RestClient
import rugds.service.CoreService

object GitAnalyticsMain extends App{

  val service = new CoreService with RestClient with gitRestClient{

    /**
      * The ExampleRestClient module provides an easy way to call the URLs
      * made available by the ExampleRestRoute module.
      */
    gitRestClient.getSomeUrl() // GET ../some/url
  }
}
