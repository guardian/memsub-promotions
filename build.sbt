name := """promotions-tool"""

version := "1.0-SNAPSHOT"

import scala.sys.process._

def commitId(): String = try {
  "git rev-parse HEAD".!!.trim
} catch {
  case _: Exception => "unknown"
}

lazy val root = (project in file(".")).enablePlugins(
  PlayScala,
  BuildInfoPlugin,
  RiffRaffArtifact,
  JDebPackaging
).settings(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    BuildInfoKey.constant("gitCommitId", Option(System.getenv("BUILD_VCS_NUMBER")) getOrElse commitId()),
    BuildInfoKey.constant("buildNumber", Option(System.getenv("BUILD_NUMBER")) getOrElse "DEV"),
    BuildInfoKey.constant("buildTime", System.currentTimeMillis)
  ),
  buildInfoPackage := "app",
  buildInfoOptions += BuildInfoOption.ToMap
)

enablePlugins(SystemdPlugin)

debianPackageDependencies := Seq("openjdk-8-jre-headless")

packageSummary := "Memsub-promotions"
packageDescription := """Memsub-promotions tool"""
maintainer := "Membership Dev <membership.dev@theguardian.com>"


riffRaffPackageType := (packageBin in Debian).value
riffRaffPackageName := "promotions-tool"
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null",
  "-J-XX:MaxRAMFraction=2",
  "-J-XX:InitialRAMFraction=2",
  "-J-XX:MaxMetaspaceSize=500m",
  "-J-XX:+PrintGCDetails",
  "-J-XX:+PrintGCDateStamps",
  s"-J-Xloggc:/var/log/${packageName.value}/gc.log"
)

scalaVersion := "2.11.8"
scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalaz" %% "scalaz-core" % "7.2.7",
  "com.gu" %% "membership-common" % "0.517",
  "com.gu" %% "play-googleauth" % "0.7.5",
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.2",
  "com.softwaremill.macwire" %% "proxy" % "2.2.2",
  "io.netty" % "netty" % "3.10.3.Final",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers ++= Seq(
  "Guardian Github Releases" at "https://guardian.github.io/maven/repo-releases",
  "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("releases"))

addCommandAlias("devrun", "run -Dconfig.resource=DEV.conf 9500")
addCommandAlias("fast-test", "testOnly -- -l Acceptance")
addCommandAlias("play-artifact", "riffRaffNotifyTeamcity")
