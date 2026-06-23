package ru.krivitskaya.anna.review

object ForComp extends App {
  val composedOption: Option[Int] =
    for {
      first  <- Some(1)
      _ = println("Going to second option")
      second <- Some(2)
    } yield first + second

  println(composedOption)

  val composedOptionDesugared: Option[Int] =
    Some(1)
      .map { first => val _ = println("Going to second option"); first }
      .flatMap(first =>
        Some(2)
          .map(second => first + second)
      )

  println(composedOptionDesugared)

  val failFastOption: Option[Int] =
    for {
      first  <- Option.empty[Int]
      _ = println("Going to second option")
      second <- Some(2)
    } yield first + second

  println(failFastOption)
}
