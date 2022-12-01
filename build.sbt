import sbt.Keys.publishMavenStyle

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3" cross CrossVersion.binary)

name := "anetabtc.io"
version := "0.1"
scalaVersion := "2.12.10"

lazy val sonatypePublic = "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"
lazy val sonatypeReleases = "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
lazy val sonatypeSnapshots = "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"


resolvers ++= Seq(Resolver.mavenLocal, sonatypeReleases, sonatypeSnapshots, Resolver.mavenCentral)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
)

libraryDependencies ++= Seq(
  "org.graalvm.sdk" % "graal-sdk" % "22.2.0",
  "org.ergoplatform" %% "ergo-appkit" % "4.0.10",
  "org.ergoplatform" %% "ergo-playground-env" % "0.0.0-88-a781666a-SNAPSHOT",
  "org.slf4j" % "slf4j-nop" % "2.0.3",
)

ThisBuild / publishMavenStyle := true
ThisBuild / publishArtifact := false

Test / fork := false

assembly / test := {}
assembly / assemblyJarName := s"anetabtc-tool-${version.value}.jar"
assembly / mainClass := Option("anetabtc.io.Main")
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}