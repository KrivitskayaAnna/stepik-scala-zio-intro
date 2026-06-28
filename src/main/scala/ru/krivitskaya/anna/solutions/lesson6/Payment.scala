package ru.krivitskaya.anna.solutions.lesson6

import zio._

object Payment extends ZIOAppDefault {
  case class Payment(userId: Int, amount: Double)

  sealed trait PaymentError
  case class InvalidAmount(amount: Double)                           extends PaymentError
  case class InsufficientFunds(requested: Double, available: Double) extends PaymentError

  def validatePayment(payment: Payment): IO[PaymentError, Payment] =
    if (payment.amount > 0) ZIO.succeed(payment)
    else ZIO.fail(InvalidAmount(payment.amount))

  def checkBalance(payment: Payment): IO[PaymentError, Payment] =
    if (payment.amount <= 5000) ZIO.succeed(payment)
    else ZIO.fail(InsufficientFunds(payment.amount, 5000.0))

  def executePayment(payment: Payment): UIO[String] =
    ZIO.succeed(s"Receipt #${payment.userId} for ${payment.amount}")

  def processPayment(payment: Payment): UIO[String] =
    (for {
      validated <- validatePayment(payment)
      checked   <- checkBalance(validated)
      receipt   <- executePayment(checked)
    } yield receipt).foldZIO(
      {
        case InvalidAmount(a) => ZIO.succeed(s"Invalid amount: $a")
        case InsufficientFunds(req, avl) =>
          ZIO.succeed(s"Insufficient funds: requested $req, available $avl")
      },
      receipt => ZIO.succeed(receipt)
    )

  override def run =
    for {
      r1 <- processPayment(Payment(1, 100.0))
      _  <- Console.printLine(s"Payment(1, 100.0): $r1")
      r2 <- processPayment(Payment(2, -50.0))
      _  <- Console.printLine(s"Payment(2, -50.0): $r2")
      r3 <- processPayment(Payment(3, 10000.0))
      _  <- Console.printLine(s"Payment(3, 10000.0): $r3")
    } yield ()
}
