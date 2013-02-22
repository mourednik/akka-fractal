import sbt._
import Keys._

object test extends Build {

  val projectName = "akka-fractal"

  override lazy val settings = super.settings ++ Seq(resolvers := Seq())

  val akkaVersion = "2.1.0"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
  val sprayJSON = "io.spray" %% "spray-json" % "1.2.3"

  val publishedScalaSettings = Seq(
    scalaVersion := "2.10.0",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
      "Spray repo" at "http://repo.spray.io"),

    libraryDependencies ++= Seq(akkaActor, akkaRemote, akkaTestkit, scalaTest, sprayJSON))

  lazy val root = Project(id = projectName,
    base = file("."),
    settings = Project.defaultSettings ++ publishedScalaSettings)
}
