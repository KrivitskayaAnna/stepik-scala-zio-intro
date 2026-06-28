package ru.krivitskaya.anna.solutions.lesson7

import zio._

object DownloadPar extends ZIOAppDefault {
  val urls: List[String] = List(
    "https://api.example.com/users",
    "https://api.example.com/orders",
    "https://api.example.com/products",
    "https://api.example.com/reviews",
    "https://api.example.com/stats"
  )

  def download(url: String): Task[String] =
    ZIO.logInfo(s"Downloading $url") *>
      ZIO.sleep(1.second).as(s"Data from $url")

  def downloadSequential(urls: List[String]): Task[List[String]] =
    ZIO.foreach(urls)(download)

  def downloadParallel(urls: List[String]): Task[List[String]] =
    ZIO.foreachPar(urls)(download)

  def downloadLimited(urls: List[String]): Task[List[String]] =
    ZIO.foreachPar(urls)(download).withParallelism(4)

  def runEffectWithLogging(effect: List[String] => Task[List[String]]): Task[Unit] =
    effect(urls).timed.flatMap {
      case (duration, result) =>
        Console.printLine(s"${result.size} results in ${duration.toMillis}ms")
    }

  override def run =
    for {
      _ <- Console.printLine("=== Sequential ===")
      _ <- runEffectWithLogging(downloadSequential)
      _ <- Console.printLine("=== Parallel ===")
      _ <- runEffectWithLogging(downloadParallel)
      _ <- Console.printLine("=== Limited ===")
      _ <- runEffectWithLogging(downloadLimited)
    } yield ()
}
