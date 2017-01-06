import sbt._
import sbt.Keys._
import rugds.sbt._

object ScalaBuild extends Build {
  val systemCoreV = "0.17.4"
  val sparkVersion = "2.0.2"


  lazy val gitAnalyticsProject = defineProject(scalaProject, "git-analytics")
    .dependsOn(gitHubProject)
    .dependsOn(sparkAppProject) settings (
    mainClass in Compile := Some("rugds.git.analytics.GitAnalyticsMain"),
    libraryDependencies ++= Seq(
      "rugds" %% "service-core" % systemCoreV,
      "rugds" %% "rest" % systemCoreV
    )

  )

  lazy val gitHubProject = defineProject(akkaProject, "git-hub") settings (
      mainClass in Compile := Some("rugds.git.hub.GitHubMain"),
      libraryDependencies ++= Seq(
        "rugds" %% "service-core" % systemCoreV,
        "rugds" %% "rest" % systemCoreV,
        "org.json4s" %% "json4s-jackson" % "3.5.0"
      ) ++ logViaLog4j
  )

  lazy val sparkAppProject = defineProject(scalaProject, "spark-app") settings (
      mainClass in Compile := Some("rugds.spark.app.SparkAppMain"),
      libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % sparkVersion,
        "org.apache.spark"  % "spark-mllib_2.11" % sparkVersion
      )
  )

}