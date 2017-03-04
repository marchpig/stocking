import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "marchpig",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Stocking",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.0"
  )
