package ru.krivitskaya.anna.lessons.lesson5

import zio._

trait Database
trait Logging

trait AppError
case class DbError(msg: String) extends AppError
case class HttpError(code: Int) extends AppError

object ZioCombining {
  val dbEffect: ZIO[Database, DbError, String] = ZIO.succeed("user-from-db")

  val logEffect: ZIO[Logging, HttpError, Int] = ZIO.succeed(200)

  val combined: ZIO[Logging with Database, AppError, String] =
    for {
      user   <- dbEffect
      status <- logEffect
    } yield s"$user: $status"
}
