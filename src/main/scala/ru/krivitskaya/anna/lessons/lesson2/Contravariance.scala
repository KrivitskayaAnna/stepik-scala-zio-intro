package ru.krivitskaya.anna.lessons.lesson2

trait Serializer[-A] {
  def serialize(a: A): String
}

object Contravariance extends App {
  val animalSerializer: Serializer[Animal] = (a: Animal) => s"""{ "name": "${a.name}" }"""

  val catSerializer: Serializer[Cat] = animalSerializer

  println(catSerializer.serialize(Cat("Garfield")))
  println(animalSerializer.serialize(Dog("Scooby")))
}
