import sbt._
import sbt.Keys.{libraryDependencies, _}
import rugds.sbt._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._

object ScalaBuild extends Build {

    val systemCoreV = "0.17.4"

  lazy val gitapimoduleMain = mainProject("gitApiModule-root")
    .aggregate(gitapimoduleProject)

  lazy val gitapimoduleProject = defineProject(akkaProject, "gitApiModule") settings (
    mainClass in Compile := Some("rugds.gitApiModule.GitapimoduleMain"),
    libraryDependencies ++= Seq(
        "rugds" %% "service-core" % systemCoreV,
        "rugds" %% "rest" % systemCoreV,
        "io.spray" %%  "spray-json" % "1.3.2"
    ) ++ logViaLog4j
  )
}