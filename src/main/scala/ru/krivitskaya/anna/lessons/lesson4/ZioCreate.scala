package ru.krivitskaya.anna.lessons.lesson4

import zio._

import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.util.Success

case class Database()
case class User()

object ZioCreate extends App {
//  val unfailableIntEffect: ZIO[Any, Nothing, Int]        = ???
//  val envUserEffect: ZIO[Database, String, User]         = ???
//  val exceptionalUnitEffect: ZIO[Any, IOException, Unit] = ???

  val effectFromConst: UIO[Int] = ZIO.succeed(42)

  val effectWithSide = ZIO.succeed {
    println("Вычисляется!")
    42
  }

  val failable: Task[Unit] = ZIO.attempt(println("hello"))

  val effectFromFailure: IO[RuntimeException, Nothing] = ZIO.fail(new RuntimeException("Boom"))

  val effectFromDie = ZIO.die(new RuntimeException("Boom"))

  val effectFromOption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(4))

  val effectFromEither: IO[String, Int] = ZIO.fromEither(Left[String, Int]("error happened"))

  val effectFromTry: Task[String] = ZIO.fromTry(Success("success value"))

  val printEffect: IO[IOException, Unit] = zio.Console.printLine("Hello, world!")

  val readInput: IO[IOException, String] = Console.readLine

  val generateRandomEffect: UIO[Boolean] = zio.Random.nextBoolean

  val currentTimeEffect: UIO[Long] = zio.Clock.currentTime(TimeUnit.DAYS)

}
