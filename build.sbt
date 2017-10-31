name := "akka-fractal"

version := "1.0.0"

scalaVersion := "2.10.4"

val akkaVersion = "2.1.2"

resolvers ++= Seq(
  "Maven Central Server" at "http://repo1.maven.org/maven2",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository" at "http://repo.spray.io"
)

libraryDependencies  ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion % Compile,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion % Compile,
  "io.spray" %% "spray-json" % "1.2.3" % Compile,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "2.0.M5b" % Test
)

packSettings

packMain := Map("akka-fractal" -> "fractal.Main")
