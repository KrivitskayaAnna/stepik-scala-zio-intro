package ru.krivitskaya.anna.tasks.lesson9

import zio._

trait EmailService {
  def send(to: String, body: String): Task[Unit]
}

case class NotificationService(emailService: EmailService) {
  def notifyUser(userId: Int): Task[Unit] =
    emailService.send(s"user-$userId@mail.com", s"Hello, user $userId!")
}

object TestLayerTask extends ZIOAppDefault {
  // Продовая реализация: печатает "PROD: to=..., body=..."
  val prodEmailLayer: ULayer[EmailService] = ???

  // Тестовая реализация: печатает "TEST: to=..., body=..."
  val testEmailLayer: ULayer[EmailService] = ???

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
