package ru.krivitskaya.anna.lessons.lesson5

import zio._

import java.io.IOException

object ZioMethods extends ZIOAppDefault {
  val firstEffect: UIO[Int] = ZIO.succeed(10)

  val transformedFirstEffect: UIO[Int] = firstEffect.map(_ + 5)

  val tappedFirstEffect: UIO[Int] = firstEffect.tap(value => ZIO.logInfo(s"Got value $value"))

  val secondEffect: UIO[Long] = ZIO.succeed(20L)

  val sumEffect =
    transformedFirstEffect.flatMap { firstValue =>
      secondEffect.map(secondValue => firstValue + secondValue)
    }

  val sumComprehensionEffect: UIO[Long] =
    for {
      firstValue <- firstEffect
      transformedFirstValue = firstValue + 5
      secondValue <- secondEffect
      sum = transformedFirstValue + secondValue
    } yield sum

  val tupleEffect: UIO[(Int, Long)] = transformedFirstEffect.zip(secondEffect)

  val tupleParEffect: UIO[(Int, Long)] = transformedFirstEffect.zipPar(secondEffect)

  val firstThenSecond     = Console.printLine("ask") *> secondEffect
  val keepFirst: UIO[Int] = transformedFirstEffect <* secondEffect

  val parsed: IO[String, Int] =
    ZIO
      .attempt("abc".toInt)
      .mapError(e => s"Parse failed: ${e.getMessage}")

  val debuggedParsing: Task[Int] =
    ZIO
      .attempt("abc".toInt)
      .tapError(err => ZIO.logError(s"Parsing failed: ${err.getMessage}"))

  val handledPure: UIO[String] =
    ZIO
      .attempt("abc".toInt)
      .fold(
        failure = err => s"Failed: ${err.getMessage}",
        success = num => s"Parsed: $num"
      )

  val handledWithEffect: UIO[String] =
    ZIO
      .attempt("abc".toInt)
      .foldZIO(
        failure = err => ZIO.succeed(s"Failed: ${err.getMessage}"),
        success = num => ZIO.succeed(s"Parsed: $num")
      )

  val debugMode: Boolean = false

  val maybeLog: UIO[Option[Unit]] =
    ZIO.when(debugMode)(ZIO.logInfo("Debug info"))

  val conditionalEffect: UIO[Option[Int]] =
    ZIO.succeed(42).when(debugMode)

  val effectAsConst: UIO[String] = ZIO.logError("some error").as("hello world")

  val effectAsUnit: Task[Unit] = firstEffect.unit

  def consoleEffect =
    for {
      _   <- Console.printLine("Enter a number:")
      raw <- Console.readLine
    } yield raw

  val pipeline: IO[IOException, Unit] =
    for {
      raw <- consoleEffect
      parsed <- ZIO
        .attempt(raw.trim.toInt)
        .tapError(err => ZIO.logError(s"Bad input: ${err.getMessage}"))
        .mapError(e => new IOException(s"Parse failed: ${e.getMessage}"))
      _ <- ZIO.when(parsed < 0)(Console.printLine("Warning: negative number"))
      result = parsed * 2
      _ <- Console.printLine(s"Result: $result")
    } yield ()

  def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    pipeline.foldZIO(
      failure = err => Console.printLine(s"Error: ${err.getMessage}"),
      success = _ => ZIO.unit
    )
}
