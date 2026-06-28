package ru.krivitskaya.anna.tasks.lesson7

import zio._

case class RegistrationForm(
    username: String,
    email: String,
    password: String,
    age: Int
)

object ValidateTask extends ZIOAppDefault {
  def checkUsername(form: RegistrationForm): IO[String, Option[Unit]] =
    ZIO.fail("Username must be at least 3 characters").unless(form.username.length >= 3)

  def checkEmail(form: RegistrationForm): IO[String, Option[Unit]] =
    ZIO.fail("Email must contain @").unless(form.email.contains("@"))

  def checkPassword(form: RegistrationForm): IO[String, Option[Unit]] =
    ZIO.fail("Password must be at least 8 characters").unless(form.password.length >= 8)

  def checkAge(form: RegistrationForm): IO[String, Option[Unit]] =
    ZIO.fail("Must be 18 or older").unless(form.age >= 18)

  // Проверьте все правила, соберите ошибки, верните список всех ошибок при неудаче
  def validateForm(form: RegistrationForm): IO[List[String], Unit] = ???

  val validForm   = RegistrationForm("alice", "alice@mail.com", "secure123", 25)
  val invalidForm = RegistrationForm("ab", "bad-email", "123", 15)

  override def run =
    for {
      _ <- Console.printLine("=== Valid form ===")
      _ <- validateForm(validForm).foldZIO(
        failure = errs => Console.printLine(s"Errors: $errs"),
        success = _ => Console.printLine("Registration OK")
      )
      _ <- Console.printLine("=== Invalid form ===")
      _ <- validateForm(invalidForm).foldZIO(
        failure = errs => Console.printLine(s"Errors: $errs"),
        success = _ => Console.printLine("Registration OK")
      )
    } yield ()
}
