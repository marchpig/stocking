import Dependencies._

lazy val root = (project in file(".")).
  enablePlugins(PlayScala).
  settings(
    inThisBuild(List(
      organization := "marchpig",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Stocking",
    libraryDependencies += filters,
    libraryDependencies += scalaTestPlusPlay % Test,
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.14.0"
  )
