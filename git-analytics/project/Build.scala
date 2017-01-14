import sbt.{ExclusionRule, _}
import sbt.Keys._
import rugds.sbt._

object ScalaBuild extends Build {
  val systemCoreV = "0.17.4"
  val sparkVersion = "2.0.2"
  val json4sVersion = "3.4.2"


  lazy val gitAnalyticsProject = defineProject(akkaProject, "git-analytics")
    .dependsOn(gitHubProject)
    .dependsOn(sparkAppProject) settings (
    mainClass in Compile := Some("rugds.git.analytics.GitAnalyticsMain"),
    libraryDependencies ++= Seq(
      "rugds"       %% "service-core"    % systemCoreV,
      "rugds"       %% "rest"            % systemCoreV
    )
  )

  lazy val gitHubProject = defineProject(akkaProject, "git-hub")
    .dependsOn(gitLib) settings (
    libraryDependencies ++= Seq(
        "rugds"       %% "service-core"   % systemCoreV ,
        "rugds"       %% "rest"           % systemCoreV ,
        "org.json4s"  %% "json4s-jackson" % json4sVersion
      ) ++ logViaLog4j
  )

  lazy val sparkAppProject = defineProject(scalaProject, "spark-app")
    .dependsOn(gitLib) settings (
    libraryDependencies ++= Seq(
        "rugds"             %% "service-core"       % systemCoreV,
        "org.apache.spark"  %% "spark-core"         % sparkVersion excludeAll ExclusionRule(organization = "org.slf4j"),
        "org.apache.spark"  %% "spark-streaming"    % sparkVersion excludeAll ExclusionRule(organization = "org.slf4j"),
        "org.apache.spark"  %% "spark-mllib"        % sparkVersion,
        "org.json4s"        %% "json4s-jackson"     % json4sVersion
    )
  )

  lazy val gitLib = defineProject(scalaProject, "git-lib")

}