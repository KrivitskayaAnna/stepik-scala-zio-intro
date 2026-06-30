package ru.krivitskaya.anna.solutions.lesson9

import zio._

trait EmailService {
  def send(to: String, body: String): Task[Unit]
}

case class NotificationService(emailService: EmailService) {
  def notifyUser(userId: Int): Task[Unit] =
    emailService.send(s"user-$userId@mail.com", s"Hello, user $userId!")
}

object TestLayer extends ZIOAppDefault {
  // Продовая реализация: печатает "PROD: to=..., body=..."
  val prodEmailLayer: ULayer[EmailService] =
    ZLayer.succeed(
      new EmailService {
        override def send(to: String, body: String): Task[Unit] =
          ZIO.logInfo(s"PROD: to=$to, body=$body")
      }
    )

  // Тестовая реализация: печатает "TEST: to=..., body=..."
  val testEmailLayer: ULayer[EmailService] =
    ZLayer.succeed(
      new EmailService {
        override def send(to: String, body: String): Task[Unit] =
          ZIO.logInfo(s"TEST: to=$to, body=$body")
      }
    )

  val notificationLayer: URLayer[EmailService, NotificationService] =
    ZLayer.fromFunction(NotificationService.apply _)

  val program: ZIO[NotificationService, Throwable, Unit] =
    ZIO.serviceWithZIO[NotificationService](_.notifyUser(42))

  override def run =
    for {
      _ <- Console.printLine("=== Production ===")
      _ <- program.provide(prodEmailLayer, notificationLayer)
      _ <- Console.printLine("=== Test ===")
      _ <- program.provide(testEmailLayer, notificationLayer)
    } yield ()
}
