package ru.krivitskaya.anna.solutions.lesson6

import zio._

object LoadConfig extends ZIOAppDefault {
  def fromFile(path: String): Task[String] =
    ZIO.fail(new java.io.FileNotFoundException(s"$path not found"))

  def fromEnv(name: String): Task[String] =
    ZIO.fail(new RuntimeException(s"Env $name is not set"))

  def defaultConfig: UIO[String] =
    ZIO.succeed("default-config-value")

  // Реализуйте: пробуйте загрузить из источников по очереди. Каждую неудачу залогируйте через tapError. Используйте orElse
  def loadConfig: UIO[String] =
    fromFile("config.yaml")
      .tapError(e => ZIO.logError(s"File source failed: ${e.getMessage}"))
      .orElse(
        fromEnv("APP_CONFIG")
          .tapError(e => ZIO.logError(s"Env source failed: ${e.getMessage}"))
      )
      .orElse(defaultConfig)

  override def run =
    loadConfig.flatMap(cfg => Console.printLine(s"Config: $cfg").orDie)
}
