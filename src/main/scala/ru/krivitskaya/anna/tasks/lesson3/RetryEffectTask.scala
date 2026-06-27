package ru.krivitskaya.anna.tasks.lesson3

object RetryEffectTask extends App {

  // Реализуйте: эффект, который выполнит action ровно n раз. При n <= 0 ничего не делать
  def repeatN(action: Effect[Unit], n: Int): Effect[Unit] = ???

  def printLine(msg: String): Effect[Unit] = Effect.pure(println(msg))
  val input                                = scala.io.StdIn.readLine().trim.toInt
  val program                              = repeatN(printLine("ping"), input)
  program.run()
}
