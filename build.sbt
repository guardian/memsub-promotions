name := """memsub-promotions"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(
  PlayScala,
  BuildInfoPlugin
).settings(
  magentaPackageName := "promotions-tool",
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    BuildInfoKey.constant("gitCommitId", Option(System.getenv("BUILD_VCS_NUMBER")) getOrElse (try {
      "git rev-parse HEAD".!!.trim
    } catch {
      case e: Exception => "unknown"
    })),
    BuildInfoKey.constant("buildNumber", Option(System.getenv("BUILD_NUMBER")) getOrElse "DEV"),
    BuildInfoKey.constant("buildTime", System.currentTimeMillis)
  ),
  buildInfoPackage := "app",
  buildInfoOptions += BuildInfoOption.ToMap
)


scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "com.gu" %% "membership-common" % "0.204-SNAPSHOT",
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.2",
  "com.softwaremill.macwire" %% "proxy" % "2.2.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

// membership-common has macros compiled against play-json 2.4.6...
dependencyOverrides += "com.typesafe.play" %% "play-json" % "2.4.6"

resolvers ++= Seq(
  "Guardian Github Releases" at "https://guardian.github.io/maven/repo-releases",
  "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("releases"))

addCommandAlias("devrun", "run -Dconfig.resource=DEV.conf 9500")
addCommandAlias("fast-test", "testOnly -- -l Acceptance")

playArtifactDistSettings
