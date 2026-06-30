package ru.krivitskaya.anna.solutions.lesson8

import zio._

object Exponential extends ZIOAppDefault {
  var callCount = 0

  def unreliableService: Task[String] = ZIO.attempt {
    callCount += 1
    if (callCount < 5) throw new RuntimeException(s"Fail #$callCount")
    else s"OK on attempt $callCount"
  }

  def fetchWithBackoff: IO[String, String] =
    unreliableService
      .tapError(e => ZIO.logError(s"Attempt failed: ${e.getMessage}"))
      .retry(Schedule.exponential(200.millis) && Schedule.recurs(6))
      .orElseFail("Service unavailable")

  override def run =
    fetchWithBackoff.debug
}
