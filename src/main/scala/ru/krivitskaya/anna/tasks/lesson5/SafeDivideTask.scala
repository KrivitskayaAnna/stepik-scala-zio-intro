package ru.krivitskaya.anna.tasks.lesson5

import zio._

object SafeDivideTask extends ZIOAppDefault {
  def divide(a: Int, b: Int): IO[String, Int] =
    if (b != 0) ZIO.succeed(a / b)
    else ZIO.fail("division by zero")

  def safeMessage(a: Int, b: Int): UIO[String] = ???

  def safeWithLog(a: Int, b: Int): UIO[String] = ???

  def safeWithDefault(a: Int, b: Int): UIO[Int] = ???

  override def run =
    for {
      r1 <- safeMessage(10, 3)
      _  <- Console.printLine(r1)
      r2 <- safeMessage(10, 0)
      _  <- Console.printLine(r2)
      r3 <- safeWithLog(10, 0)
      _  <- Console.printLine(r3)
      r4 <- safeWithDefault(10, 0)
      _  <- Console.printLine(s"Default: $r4")
    } yield ()
}
