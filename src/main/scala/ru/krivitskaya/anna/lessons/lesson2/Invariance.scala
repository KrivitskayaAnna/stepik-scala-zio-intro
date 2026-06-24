package ru.krivitskaya.anna.lessons.lesson2

class MutableBox[A](var content: A)

trait Animal {
  def name: String
}

case class Cat(name: String) extends Animal

case class Dog(name: String) extends Animal

object Invariance extends App {
  val cat: Cat = Cat("Garfield1")

  val dog: Dog = Dog("Scooby")

  val inboxCat: MutableBox[Cat] = new MutableBox[Cat](cat)

  val inboxAnimal: MutableBox[Animal] = new MutableBox[Animal](cat)
//  fails: val inboxAnimal: MutableBox[Animal] = inboxCat

  inboxAnimal.content = dog
  //fails: inboxCat.content = dog
}
