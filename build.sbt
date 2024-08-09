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

packageSummary := "Memsub-promotions"
packageDescription := """Memsub-promotions tool"""
maintainer := "Membership Dev <membership.dev@theguardian.com>"


riffRaffPackageType := (packageBin in Debian).value
riffRaffPackageName := "promotions-tool"
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")

riffRaffArtifactResources += (file("cloudformation/memsub-promotions-cf.yaml"), "cfn/cfn.yaml")

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null",
  "-J-XX:MaxMetaspaceSize=500m",
  "-J-XX:+PrintGCDetails",
  s"-J-Xlog:gc:/var/log/${packageName.value}/gc.log"
)

scalaVersion := "2.13.10"
scalacOptions ++= Seq("-feature")

val awsVersion = "1.12.649"

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  "com.gu.play-googleauth" %% "play-v30" % "4.0.0",
  "com.softwaremill.macwire" %% "macros" % "2.5.0" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.5.0",
  "com.softwaremill.macwire" %% "proxy" % "2.5.0",
  "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-dynamodb" % awsVersion,
  "org.scalaz" %% "scalaz-core" % "7.3.7",
  "com.github.nscala-time" %% "nscala-time" % "2.32.0",
  "com.gu" %% "support-internationalisation" % "0.16",
  "io.lemonlabs" %% "scala-uri" % "4.0.3",
  "com.squareup.okhttp3" % "okhttp" % "4.10.0",
  "com.typesafe.play" %% "play-json-joda" % "2.10.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
)

dependencyOverrides ++= List(
  // Play still uses an old version of jackson-core which has a vulnerability - https://security.snyk.io/vuln/SNYK-JAVA-COMFASTERXMLJACKSONCORE-7569538
  "com.fasterxml.jackson.core" % "jackson-core" % "2.17.2"
)

resolvers ++= Seq(
  "Guardian Github Releases" at "https://guardian.github.io/maven/repo-releases",
  "Guardian Github Snapshots" at "https://guardian.github.com/maven/repo-snapshots",
  Resolver.sonatypeRepo("releases"))

addCommandAlias("devrun", "run -Dconfig.resource=DEV.conf 9500")
addCommandAlias("fast-test", "testOnly -- -l Acceptance")
addCommandAlias("play-artifact", "riffRaffNotifyTeamcity")
