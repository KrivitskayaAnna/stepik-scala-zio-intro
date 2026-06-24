package ru.krivitskaya.anna.solutions.lesson3

case class Effect[A](run: () => A) {
  def map[B](f: A => B): Effect[B]             = Effect(() => f(run()))
  def flatMap[B](f: A => Effect[B]): Effect[B] = f(run())
}

object Effect {
  def pure[A](a: => A): Effect[A] = Effect(() => a)
}

object MyEffectPipeline extends App {
  def readLine: Effect[String] = Effect.pure(scala.io.StdIn.readLine())
  def parseInt(s: String): Effect[Int] = Effect.pure(s.trim.toInt)
  def double(n: Int): Effect[Int] = Effect.pure(n * 2)
  def printResult(n: Int): Effect[Unit] = Effect.pure(println(s"Result: $n"))

  //Соберите программу из шагов выше, используя for-comprehension, и запустите её
  val program: Effect[Unit] =
    for {
      line   <- readLine
      number <- parseInt(line)
      doubled <- double(number)
      _      <- printResult(doubled)
    } yield ()

  program.run()
}
