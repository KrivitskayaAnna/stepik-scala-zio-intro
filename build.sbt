ThisBuild / version := "1.0"

ThisBuild / scalaVersion := "2.13.10"

lazy val zioVersion = "2.1.12"

lazy val zioDependencies = Seq(
  "dev.zio" %% "zio" % zioVersion
)

lazy val testZioDependencies = Seq(
  "dev.zio" %% "zio-test",
  "dev.zio" %% "zio-test-sbt"
).map(_ % zioVersion % Test)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

lazy val root = (project in file("."))
  .settings(
    name := "zio-intro",
    organization := "ru.krivitskaya.anna",
    libraryDependencies ++= Seq(
      zioDependencies,
      testZioDependencies
    ).reduce(_ ++ _),
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
//      "-Xfatal-warnings"
    )
  )
