package ru.krivitskaya.anna.tasks.lesson8

import zio._

object PollTask extends ZIOAppDefault {
  var pollCount = 0

  def checkStatus: UIO[String] =
    ZIO
      .succeed {
        pollCount += 1
        if (pollCount < 4) "pending" else "completed"
      }
      .tap(s => ZIO.logInfo(s"Poll #$pollCount: $s"))

  def pollUntilComplete: IO[String, String] = ???

  override def run = pollUntilComplete.debug
}
