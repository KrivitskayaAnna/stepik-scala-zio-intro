package ru.krivitskaya.anna.tasks.lesson8

import zio._

object ExponentialTask extends ZIOAppDefault {
  var callCount = 0

  def unreliableService: Task[String] = ZIO.attempt {
    callCount += 1
    if (callCount < 5) throw new RuntimeException(s"Fail #$callCount")
    else s"OK on attempt $callCount"
  }

  def fetchWithBackoff: IO[String, String] = ???

  override def run =
    fetchWithBackoff.flatMap(r => Console.printLine(s"Result: $r"))
}
