package ru.krivitskaya.anna.lessons.lesson4

object ZIOLegacyRun extends App {
  def program = zio.Console.printLine("Hello, legacy world!")

  def run = zio.Unsafe.unsafe { implicit unsafe =>
    zio.Runtime.default.unsafe.run(program).getOrThrowFiberFailure()
  }

  List(1, 2, 3).map(_ => run)
}
