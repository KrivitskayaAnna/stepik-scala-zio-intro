package ru.krivitskaya.anna.solutions.lesson8

import zio._

object Poll extends ZIOAppDefault {
  var pollCount = 0

  def checkStatus: UIO[String] =
    ZIO
      .succeed {
        pollCount += 1
        if (pollCount < 4) "pending" else "completed"
      }
      .tap(s => ZIO.logInfo(s"Poll #$pollCount: $s"))

  def pollUntilComplete: IO[String, String] =
    checkStatus
      .flatMap {
        case "completed" => ZIO.succeed("completed")
        case other       => ZIO.fail(other)
      }
      .retry(Schedule.spaced(2.seconds) && Schedule.recurs(5))
      .orElseFail("Timed out waiting for completion")

  override def run = pollUntilComplete.debug
}
