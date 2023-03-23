ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "fs2-jdbc"
  )

libraryDependencies += "co.fs2" %% "fs2-core" % "3.6.1"
libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
libraryDependencies += "com.h2database" % "h2" % "2.1.214" % Test
