ThisBuild / version := "1.0"

ThisBuild / scalaVersion := "2.13.10"

lazy val zioVersion = "2.1.12"

lazy val zioDependencies = Seq(
  "dev.zio" %% "zio"      % zioVersion
)

lazy val catsDependencies = Seq(
  "dev.zio" %% "zio"      % zioVersion
)

lazy val testZioDependencies = Seq(
  "dev.zio" %% "zio-test",
  "dev.zio" %% "zio-test-sbt"
).map(_ % zioVersion % Test)

lazy val root = (project in file("."))
  .settings(
    name := "zio-intro",
    organization := "ru.krivitskaya.anna",
    libraryDependencies ++= Seq(
      zioDependencies, testZioDependencies
    ).reduce(_ ++ _)
  )
