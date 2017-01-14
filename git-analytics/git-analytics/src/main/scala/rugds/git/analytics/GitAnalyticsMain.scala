package rugds.git.analytics

import rugds.auth.Auth
import rugds.git.hub.gitRestClient
import rugds.rest.client.RestClient
import rugds.service.CoreService
import rugds.spark.app.sparkAppClient


object GitAnalyticsMain extends App{

  val service = new CoreService with RestClient with Auth with gitRestClient with sparkAppClient {


    gitRestClient.GitHubApiMagic()

    sparkAppClient.sparkMagic("rug-wacc")
  }

}
