package ru.krivitskaya.anna.tasks.lesson9

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

object DependencyGraphTask extends ZIOAppDefault {
  val configLayer: ULayer[Config]                              = ???

  val loggerLayer: ULayer[Logger]                              = ???

  val appServiceLayer: URLayer[Config with Logger, AppService] = ???

  val program: ZIO[AppService, Throwable, Unit] = ???

  override def run = ???
}
