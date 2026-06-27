package ru.krivitskaya.anna.tasks.lesson5

import zio._

object ParallelTask extends ZIOAppDefault {
  def fetchFromService(name: String): UIO[String] =
    ZIO.sleep(1.second).as(s"data from $name")

  // Последовательно: получите данные от сервисов с названиями A, B, C и верните их через запятую
  def fetchSequential: UIO[String] = ???

  // Параллельно: получите данные от всех трёх сервисов одновременно
  def fetchParallel: UIO[String] = ???

  override def run =
    for {
      _ <- Console.printLine("Sequential:")
      _ <- fetchSequential.timed.flatMap {
        case (duration, result) =>
          Console.printLine(s"  $result (${duration.toMillis}ms)")
      }
      _ <- Console.printLine("Parallel:")
      _ <- fetchParallel.timed.flatMap {
        case (duration, result) =>
          Console.printLine(s"  $result (${duration.toMillis}ms)")
      }
    } yield ()
}
