package ru.krivitskaya.anna.lessons.lesson4

import zio._

object ZIORun extends ZIOAppDefault {
  val program = ZIO.succeed("124alkfsj".toInt)//.mapError(err => "Oops, there is an error")
  //ZIO.attempt("124alkfsj".toInt)//.mapError(err => "Oops, there is an error")
    //zio.Console.printLine("Hello, legacy world!")

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = program

  List(1, 2, 3).map(_ => run)
}
