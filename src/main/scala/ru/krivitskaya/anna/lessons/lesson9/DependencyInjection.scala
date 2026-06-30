package ru.krivitskaya.anna.lessons.lesson9

import zio._

case class Work(jobName: String)

case class Hobby(hobbyName: String)

case class Person(work: Work, hobby: Hobby) {
  def printPerson: Task[Unit] =
    Console.printLine(s"A person with job ${work.jobName} and hobby ${hobby.hobbyName}")
}

object DependencyInjection extends ZIOAppDefault {
  val work: Work     = Work("Scala dev")
  val hobby: Hobby   = Hobby("Swimming")
  val person: Person = Person(work, hobby)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    person.printPerson.debug
}
