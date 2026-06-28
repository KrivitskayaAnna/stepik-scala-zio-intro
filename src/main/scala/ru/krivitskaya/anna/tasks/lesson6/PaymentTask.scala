package ru.krivitskaya.anna.tasks.lesson6

import zio._

object PaymentTask extends ZIOAppDefault {
  case class Payment(userId: Int, amount: Double)

  sealed trait PaymentError
  case class InvalidAmount(amount: Double)                           extends PaymentError
  case class InsufficientFunds(requested: Double, available: Double) extends PaymentError

  def validatePayment(payment: Payment): IO[PaymentError, Payment] = ???

  def checkBalance(payment: Payment): IO[PaymentError, Payment] = ???

  def executePayment(payment: Payment): UIO[String] = ???

  // Соберите пайплайн: validate → check → execute. При ошибке верните строку с описанием проблемы:
  // InvalidAmount       → "Invalid amount: <amount>"
  // InsufficientFunds   → "Insufficient funds: requested <requested>, available <available>"
  // При успехе верните receipt как есть.
  def processPayment(payment: Payment): UIO[String] = ???

  override def run =
    for {
      r1 <- processPayment(Payment(1, 100.0))
      _  <- Console.printLine(s"Payment(1, 100.0): $r1").orDie
      r2 <- processPayment(Payment(2, -50.0))
      _  <- Console.printLine(s"Payment(2, -50.0): $r2").orDie
      r3 <- processPayment(Payment(3, 10000.0))
      _  <- Console.printLine(s"Payment(3, 10000.0): $r3").orDie
    } yield ()
}
