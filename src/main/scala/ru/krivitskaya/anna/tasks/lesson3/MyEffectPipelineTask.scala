package ru.krivitskaya.anna.tasks.lesson3

case class Effect[A](run: () => A) {
  def map[B](f: A => B): Effect[B]             = Effect(() => f(run()))
  def flatMap[B](f: A => Effect[B]): Effect[B] = f(run())
}

object Effect {
  def pure[A](a: => A): Effect[A] = Effect(() => a)
}

object MyEffectPipelineTask extends App {
  def readLine: Effect[String]          = Effect.pure(scala.io.StdIn.readLine())
  def parseInt(s: String): Effect[Int]  = ???
  def double(n: Int): Effect[Int]       = ???
  def printResult(n: Int): Effect[Unit] = ???

  //Соберите программу из шагов выше, используя for-comprehension, и запустите её
  ???
}
