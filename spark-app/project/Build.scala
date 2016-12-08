import sbt._
import sbt.Keys.{libraryDependencies, _}
import rugds.sbt._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._

object ScalaBuild extends Build {
    val sparkVersion = "2.0.2"

  lazy val sparkappMain = mainProject("spark-app-root")
    .aggregate(sparkappProject)

  lazy val sparkappProject = defineProject(scalaProject, "spark-app") settings (
    mainClass in Compile := Some("rugds.sparkapp.SparkappMain"),
    libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % sparkVersion,
        "org.apache.spark"  % "spark-mllib_2.11" % sparkVersion
    )
  )
}