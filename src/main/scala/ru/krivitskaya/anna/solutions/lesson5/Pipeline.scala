package ru.krivitskaya.anna.solutions.lesson5

import zio._

object Pipeline extends ZIOAppDefault {
  def readName: IO[String, String] = Console.readLine.mapError(_.getLocalizedMessage)

  def readAge: IO[String, Int] =
    Console.readLine
      .flatMap(str => ZIO.attempt(str.toInt))
      .mapError(_.getLocalizedMessage)

  def validateAge(age: Int): IO[String, Int] =
    if (age < 1 || age > 150) ZIO.fail("Illegal age") else ZIO.succeed(age)

  def program: IO[String, String] =
    for {
      name         <- readName
      age          <- readAge
      validatedAge <- validateAge(age)
      _            <- ZIO.logInfo(s"User $name of age $validatedAge")
      _            <- ZIO.when(age < 18)(ZIO.logWarning("Access restricted"))
    } yield s"Name: $name, Age: $validatedAge"

  override def run =
    program.foldZIO(
      failure = err => Console.printLine(s"Error: $err"),
      success = result => Console.printLine(result)
    )
}
