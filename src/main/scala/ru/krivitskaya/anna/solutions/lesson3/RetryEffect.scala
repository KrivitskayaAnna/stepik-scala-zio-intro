package ru.krivitskaya.anna.solutions.lesson3

object RetryEffect extends App {
  def printLine(msg: String): Effect[Unit] = Effect.pure(println(msg))

  // Реализуйте: эффект, который выполнит action ровно n раз. При n <= 0 ничего не делать
  def repeatN(action: Effect[Unit], n: Int): Effect[Unit] =
    if (n <= 0) Effect.pure(())
    else action.flatMap(_ => repeatN(action, n - 1))

  val input = scala.io.StdIn.readLine().trim.toInt
  val program = repeatN(printLine("ping"), input)
  program.run()
}