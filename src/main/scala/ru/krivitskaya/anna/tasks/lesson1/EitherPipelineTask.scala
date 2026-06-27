package ru.krivitskaya.anna.tasks.lesson1

object EitherPipelineTask extends App {
  def parseUserId(raw: String): Either[String, Int] =
    raw.toIntOption.toRight(s"Invalid user id: $raw")

  def findUserName(id: Int): Either[String, String] =
    if (id > 0) Right(s"User#$id")
    else Left(s"User with id=$id not found")

  def greet(name: String): Either[String, String] =
    if (name.nonEmpty) Right(s"Hello, $name!")
    else Left("Empty name")

  def processUser(raw: String): Either[String, String] = ???

  val input = scala.io.StdIn.readLine()

  println(processUser(input))
}
