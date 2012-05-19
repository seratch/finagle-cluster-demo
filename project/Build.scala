import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "finagle-cluster-demo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "log4j" % "log4j" % "1.2.16" % "compile",
    "com.twitter" %% "finagle-core" % "4.0.2",
    "com.twitter" %% "finagle-http" % "4.0.2",
    "com.twitter" %% "finagle-serversets" % "4.0.2",
    "com.twitter" %% "util-core" % "4.0.1",
    "com.twitter.common" % "zookeeper" % "0.0.35",
    "io.netty" % "netty" % "3.4.5.Final",
    "com.github.seratch" %% "scalikejdbc" % "1.1.1",
    "com.github.seratch" %% "scalikejdbc-play-plugin" % "1.1.1",
    "org.hsqldb" % "hsqldb" % "[2,)",
    "org.scalatest" %% "scalatest" % "1.7.2" % "test"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository")),
    resolvers += "twitter" at "http://maven.twttr.com/"
  )

}
