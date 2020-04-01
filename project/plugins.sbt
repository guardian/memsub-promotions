// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.25")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.10")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.2")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.12")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.4")

addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.1.3")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

libraryDependencies += "org.vafer" % "jdeb" % "1.5" artifacts (Artifact("jdeb", "jar", "jar"))
