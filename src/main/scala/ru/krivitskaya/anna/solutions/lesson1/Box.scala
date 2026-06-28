package ru.krivitskaya.anna.solutions.lesson1

case class Box[A](value: A) {
  def map[B](f: A => B): Box[B] = Box(f(value))

  def flatMap[B](f: A => Box[B]): Box[B] = f(value)
}

object Box extends App {
  val result: Box[Int] =
    for {
      x <- Box(10)
      y <- Box(20)
      z <- Box(12)
    } yield x + y + z

  println(result)
}
