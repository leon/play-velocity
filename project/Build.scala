import sbt._
import sbt.Keys._

object ProjectBuild extends Build {

  lazy val buildVersion =  "1.0-SNAPSHOT"

  lazy val root = Project(id = "play-plugins-velocity", base = file("."), settings = Project.defaultSettings).settings(
    organization := "se.radley",
    version := buildVersion,
    scalaVersion := "2.9.1",
    resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    libraryDependencies ++= Seq(
      "play" %% "play" % "[2.0,)",
      "play" %% "play-test" % "[2.0,)",
      ("org.apache.velocity" % "velocity" % "1.7").exclude("commons-logging", "commons-logging"),
      ("org.apache.velocity" % "velocity-tools" % "2.0").exclude("commons-logging", "commons-logging")
    ),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>https://github.com/leon/play-velocity</url>
      <licenses>
        <license>
          <name>Apache 2.0</name>
          <url>http://www.opensource.org/licenses/Apache-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:leon/play-velocity.git</url>
        <connection>scm:git:git@github.com:leon/play-velocity.git</connection>
      </scm>
      <developers>
        <developer>
          <id>leon</id>
          <name>Leon Radley</name>
          <url>http://leon.radley.se</url>
        </developer>
      </developers>
    ),
    publishTo <<= version { version: String =>
      val nexus = "https://oss.sonatype.org/"
      if (version.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )
}
