package ru.krivitskaya.anna.solutions.lesson9

import zio._

case class Config(appName: String, maxRetries: Int)

case class Logger(prefix: String) {
  def log(msg: String): UIO[Unit] = ZIO.logInfo(s"[$prefix] $msg")
}

case class AppService(config: Config, logger: Logger) {
  def launch: Task[Unit] =
    for {
      _ <- logger.log(s"Starting ${config.appName}")
      _ <- logger.log(s"Max retries: ${config.maxRetries}")
      _ <- logger.log("Done!")
    } yield ()
}

object DependencyGraph extends ZIOAppDefault {
  val configLayer: ULayer[Config] = ZLayer.succeed(Config("aboba", 3))

  val loggerLayer: ULayer[Logger] = ZLayer.succeed(Logger("my_logger"))

  val appServiceLayer: URLayer[Config with Logger, AppService] =
    ZLayer.fromFunction(AppService.apply _)

  val program: ZIO[AppService, Throwable, Unit] =
    ZIO.serviceWithZIO[AppService](_.launch)

  override def run = program.provide(configLayer, loggerLayer, appServiceLayer)
}
