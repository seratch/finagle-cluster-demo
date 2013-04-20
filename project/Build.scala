import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "finagle-cluster-demo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.twitter"        %% "finagle-core"              % "6.3.0",
    "com.twitter"        %% "finagle-http"              % "6.3.0",
    "com.twitter"        %% "finagle-serversets"        % "6.3.0",
    "com.twitter"        %% "util-core"                 % "6.3.0",
    "com.twitter.common" %  "zookeeper"                 % "0.0.35",
    "com.github.seratch" %% "scalikejdbc"               % "[1.5,)",
    "com.github.seratch" %% "scalikejdbc-interpolation" % "[1.5,)",
    "com.github.seratch" %% "scalikejdbc-play-plugin"   % "[1.5,)",
    "org.hsqldb"         %  "hsqldb"                    % "[2,)",
    "org.scalatest"      %% "scalatest"                 % "[1.8,)" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "twitter" at "http://maven.twttr.com/"
  )

}
