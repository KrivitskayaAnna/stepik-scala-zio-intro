package ru.krivitskaya.anna.solutions.lesson1

object EitherPipeline extends App {
  def parseUserId(raw: String): Either[String, Int] =
    raw.toIntOption.toRight(s"Invalid user id: $raw")

  def findUserName(id: Int): Either[String, String] =
    if (id > 0) Right(s"User#$id")
    else Left(s"User with id=$id not found")

  def greet(name: String): Either[String, String] =
    if (name.nonEmpty) Right(s"Hello, $name!")
    else Left("Empty name")

  def processUser(raw: String): Either[String, String] =
    for {
      userId <- parseUserId(raw)
      userName <- findUserName(userId)
      greeting <- greet(userName)
    } yield greeting

  println(processUser("42"))
  println(processUser("-1"))
  println(processUser("abc"))
}
