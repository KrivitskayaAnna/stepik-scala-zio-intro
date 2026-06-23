package ru.krivitskaya.anna.review

case class CustomStructure[A](value: A) {
  def map[B](f: A => B): CustomStructure[B] =
    flatMap((a: A) => CustomStructure.pure(f(a)))

  def flatMap[B](f: A => CustomStructure[B]): CustomStructure[B] =
    f(value)
}

object CustomStructure {
  def pure[B](a: B): CustomStructure[B] = CustomStructure(a)
}

object CustomForComp extends App {
  val composedCustom: CustomStructure[String] =
    CustomStructure("first value").flatMap { first =>
      CustomStructure("second value").map { second =>
        s"$first, $second"
      }
    }

  println(composedCustom)

  val composedCustomFor: CustomStructure[String] =
    for {
      first  <- CustomStructure("first value")
      _ = println("Going to second structure")
      second <- CustomStructure("second value")
    } yield s"$first, $second"

  println(composedCustomFor)

  val composedCustomFailingFor: CustomStructure[Int] =
    for {
      first  <- CustomStructure("first value")
      _ = println("Going to second structure")
      second <- CustomStructure("second value")
    } yield first.toInt + second.toInt

  println(composedCustomFailingFor)
}
