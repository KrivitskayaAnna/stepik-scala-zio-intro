package ru.krivitskaya.anna.lessons.lesson6

import zio._

sealed trait UserError
case class UserNotFound(id: Int)       extends UserError
case class UserBlocked(reason: String) extends UserError
case class UserBlocked1(reason: String) extends UserError

case class User(id: Int, name: String)

object ErrorModelling extends ZIOAppDefault {
  def getUser(id: Int): IO[UserError, User] =
    if (id <= 0) ZIO.fail(UserNotFound(id))
    else if (id == 13) ZIO.fail(UserBlocked("suspicious activity"))
    else if (id == 14) ZIO.fail(UserBlocked1("suspicious activity"))
    else ZIO.succeed(User(id, s"User-$id"))

  def recoverFromTypedError(error: UserError): UIO[String] = error match {
    case UserNotFound(id) => ZIO.succeed(s"User $id not found — show 404")
    case UserBlocked(r)   => ZIO.succeed(s"User blocked: $r — show 403")
  }

  val userPipeline: UIO[String] =
    getUser(14).foldZIO(
      failure => recoverFromTypedError(failure),
      success => ZIO.succeed(s"Welcome, ${success.name}")
    )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = userPipeline.debug
}
