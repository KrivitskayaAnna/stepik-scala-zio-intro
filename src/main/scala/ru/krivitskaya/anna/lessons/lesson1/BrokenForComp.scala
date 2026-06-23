package ru.krivitskaya.anna.lessons.lesson1

case class BrokenCustomStructure[A](value: A) {
  def map[B](f: A => B): BrokenCustomStructure[B] =
    BrokenCustomStructure.pure(f(value))

  def flatMap[B](f: A => BrokenCustomStructure[B]): BrokenCustomStructure[A] =
    this
}

object BrokenCustomStructure {
  def pure[B](a: B): BrokenCustomStructure[B] = BrokenCustomStructure(a)
}
// законы:
// pure(a).flatMap(f) == f(a)
// m.flatMap(pure) == m
// m.flatMap(f).flatMap(g) == m.flatMap(a => g(f(a)))

object BrokenForComp extends App {
  val composedBroken =
    for {
      first <- BrokenCustomStructure("first value")
      _ = println(s"Broken got first: $first")
      second <- BrokenCustomStructure("second value")
      _ = println(s"Broken got second: $second")
    } yield first.toInt + second.toInt

  println(composedBroken)

  //left identity broken
  val a = "hello"
  val f = (s: String) => BrokenCustomStructure(s.length)

  val left: BrokenCustomStructure[String] = BrokenCustomStructure.pure(a).flatMap(f) // BrokenCustomStructure("hello")
  val right: BrokenCustomStructure[Int]   = f(a)                                     // BrokenCustomStructure(5)
  println(left == right)
}
