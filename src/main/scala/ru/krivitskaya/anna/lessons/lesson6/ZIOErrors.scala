package ru.krivitskaya.anna.lessons.lesson6

import zio._

import java.sql.SQLException

object ZIOErrors extends ZIOAppDefault {

  val anEffect: Task[Int] = ZIO.fail(new RuntimeException("Boom"))

  val mappedErrorEffect: Task[Int] =
    anEffect.mapError(throwable => new SQLException("string_error"))

  val flatmappedErrorEffect: IO[SQLException, Int] =
    anEffect.flatMapError { throwable =>
      val errorMessage = s"Error with msg ${throwable.getLocalizedMessage}"
      ZIO.logError(errorMessage).as(new SQLException(errorMessage))
    }

  // Переключение каналов: either / absolve
  val infallibleEitherEffect: IO[Nothing, Either[Throwable, Int]] = anEffect.either

  val absolvedEffect: IO[Throwable, Int] = infallibleEitherEffect.absolve

  // Границы типизации: orDie / absorb
  val infallible: UIO[Unit] = Console.printLine("hello").orDie

  val absorbed: Task[Int] = ZIO.succeed("abc".toInt).absorb

  // Перехват ошибок: catchAll / catchSome
  val caught: Task[Int] =
    ZIO
      .attempt("abc".toInt)
      .catchAll(_ => ZIO.attempt("aboba".toInt))

  val caughtSome: Task[Int] =
    ZIO
      .attempt("abc".toInt)
      .catchSome {
        case _: NumberFormatException => ZIO.succeed(-1)
      }

  // Обработка успеха и ошибки: fold/foldZIO/foldCauseZIO
  val foldEffect1: UIO[Int] = anEffect.foldZIO(
    failure => ZIO.logError(s"Error happened \n ${failure.getLocalizedMessage}").as(10),
    success => ZIO.succeed(success)
  )

  val foldEffect: UIO[Int] = anEffect.foldCauseZIO(
    cause => ZIO.logErrorCause("Error happened", cause).as(10),
    success => ZIO.succeed(success)
  )

  // Fallback: orElse
  val fallbackEffect: UIO[String] =
    ZIO
      .readFile("primary.data")
      .orElse(ZIO.readFile("backup.data"))
      .orElse(ZIO.succeed("default content"))

  // Retry
  var attemptCount = 0

  val unstable: Task[String] = ZIO.logInfo(s"Start executing attempt $attemptCount") *>
    ZIO.attempt {
      if (attemptCount < 3) {
        attemptCount += 1
        throw new RuntimeException(s"Attempt ${attemptCount - 1} failed")
      } else s"Success on attempt $attemptCount"
    }

  val retried: Task[String] =
    unstable.retry(Schedule.recurs(10))


  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = retried.debug
}
