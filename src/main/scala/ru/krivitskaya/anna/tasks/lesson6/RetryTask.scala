package ru.krivitskaya.anna.tasks.lesson6

import zio._

object RetryTask extends ZIOAppDefault {
  var callCount = 0

  def unreliableService: Task[String] = ZIO.attempt {
    callCount += 1
    if (callCount < 4) throw new RuntimeException(s"Service unavailable (attempt $callCount)")
    else s"Data received (attempt $callCount)"
  }

  def fetchWithRetry(n: Int): UIO[String] = ???

  override def run =
    fetchWithRetry(2).flatMap(r => Console.printLine(s"Result: $r"))
}
