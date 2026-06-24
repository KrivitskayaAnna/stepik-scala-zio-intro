package ru.krivitskaya.anna.lessons.lesson2

class ImmutableBox[+A](val content: A)

object Covariance extends App {
  val cat: Cat = Cat("Garfield1")

  val dog: Dog = Dog("Scooby")

  val inboxCat: ImmutableBox[Cat] = new ImmutableBox[Cat](cat)

  val inboxAnimal: ImmutableBox[Animal] = inboxCat

  //fails: inboxAnimal.content = dog
}
