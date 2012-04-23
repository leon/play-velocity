# Velocity
This plugin is BETA and may or may not work properly, I just need some more time to test it properly...

http://velocity.apache.org/

## Installation
Start by adding the plugin, in your `project/Build.scala`

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-velocity" % "1.0-SNAPSHOT"
    )

Then we need to add a resolver so that `sbt` knows where to get it from

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "OSS Repo" at "https://oss.sonatype.org/content/repositories/snapshots"
    )

We now need to register the plugin, this is done by creating(or appending) to the `conf/play.plugins` file

    2000:se.radley.plugin.velocity.VelocityPlugin

We continue to edit the `conf/application.conf` file. We need to disable some plugins that we don't need.
Add these lines:



Check out the [sample directory](https://github.com/leon/play-velocity/tree/master/sample) and the [wiki](https://github.com/leon/play-velocity/wiki)
