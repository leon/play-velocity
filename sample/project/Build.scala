import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "sample"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-velocity" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers:= Seq(
      	"Sonatype Snapshots" at "https://oss.sonatype.org/content/groups/public/",
      	"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
      )
    )

}
