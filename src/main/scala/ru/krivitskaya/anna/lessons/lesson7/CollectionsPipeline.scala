package ru.krivitskaya.anna.lessons.lesson7

import zio._

object CollectionsPipeline extends ZIOAppDefault {
  val batchOrderIds: List[Int] = List(1, -2, 3, -4, 5, 6, -7, 8)

  def processOrderIfValid(id: Int): IO[String, String] =
    if (id > 0) ZIO.succeed(s"Order-$id processed")
    else ZIO.fail(s"Invalid order: $id")

  def processSuccessAndErrorOrders(errors: Iterable[String], successes: Iterable[String]) =
    ZIO.foreachParDiscard(errors)(err => ZIO.logWarning(s"Skipped: $err")) <&>
      ZIO.foreachPar(successes) { order =>
        ZIO.succeed(s"$order ✓").tap(r => ZIO.logInfo(r))
      }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      (errors, successes) <- ZIO.partitionPar(batchOrderIds)(processOrderIfValid)
      _                   <- processSuccessAndErrorOrders(errors, successes)
      _                   <- Console.printLine(s"Processed ${successes.size} orders, skipped ${errors.size}")
    } yield ()
}
