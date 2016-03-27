name := """wedding-bootstrap"""

version := "1.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "joda-time" % "joda-time" % "2.9.2",
  "org.apache.commons" % "commons-email" % "1.3.1",
  "com.dropbox.core" % "dropbox-core-sdk" % "2.0.1",
  "org.apache.commons" % "commons-csv" % "1.2",
  "com.google.inject.extensions" % "guice-multibindings" % "4.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

