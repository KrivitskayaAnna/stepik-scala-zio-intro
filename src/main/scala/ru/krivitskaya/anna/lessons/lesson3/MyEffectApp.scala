package ru.krivitskaya.anna.lessons.lesson3

case class Effect[A](run: () => A) {
  def map[B](f: A => B): Effect[B]             = Effect(() => f(run()))
  def flatMap[B](f: A => Effect[B]): Effect[B] = f(run())
}

object Effect {
  def pure[A](a: => A): Effect[A] = Effect(() => a)
}

object MyEffectApp extends App {
  val part2: Effect[Unit] = Effect.pure(println("world!"))

  val part1: Effect[Unit] = Effect.pure(print("Hello, "))

  val helloWorld: Effect[Unit] = part1.flatMap(_ => part2)

  helloWorld.run()

  val helloWorldFor: Effect[Unit] =
    for {
      hello <- part1
      world <- part2
    } yield ()

  helloWorldFor.run()

  //referential transparency
  val e1 = Effect.pure(println("Запрос в БД"))
  val e2 = Effect.pure(println("Запрос в БД"))
  e1.run()
  e2.run()

  val e = Effect.pure(println("Запрос в БД"))
  val e3 = e
  val e4 = e
  e3.run()
  e4.run()

  //incorrect combo - Effect of Effect
  val hello: Effect[Effect[Unit]] = part1.map(_ => part2)
  hello.run()
}
