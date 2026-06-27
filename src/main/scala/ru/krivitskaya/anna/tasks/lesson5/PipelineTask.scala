package ru.krivitskaya.anna.tasks.lesson5

import zio._

object PipelineTask extends ZIOAppDefault {
  def readName: IO[String, String] = ???

  def readAge: IO[String, Int] = ???

  def validateAge(age: Int): IO[String, Int] = ???

  def program: IO[String, String] = ???

  override def run =
    program.foldZIO(
      failure = err => Console.printLine(s"Error: $err"),
      success = result => Console.printLine(result)
    )
}
