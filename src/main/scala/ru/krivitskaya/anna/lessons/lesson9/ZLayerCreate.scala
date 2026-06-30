package ru.krivitskaya.anna.lessons.lesson9

import zio._

object ZLayerCreate extends ZIOAppDefault {
  val workLayer: ULayer[Work] = ZLayer.succeed(Work("backend developer"))

  val hobbyEffect: Task[Hobby] = ZIO.attempt(Hobby("swimming"))

  def hobbyLayer: TaskLayer[Hobby] = ZLayer.fromZIO(hobbyEffect) // или ZLayer(hobbyEffect)

  val personLayer: URLayer[Work with Hobby, Person] =
    ZLayer.fromFunction { (work: Work, hobby: Hobby) =>
      Person(work, hobby)
    }

  val shortcutPersonLayer: ZLayer[Work with Hobby, Nothing, Person] = ZLayer.fromFunction(Person.apply _)

  val composedPersonLayer: TaskLayer[Person] =
    hobbyLayer.flatMap { hobby =>
      workLayer.map { work =>
        ZEnvironment(Person(work.get, hobby.get))
      }
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    composedPersonLayer.debug.launch
}
