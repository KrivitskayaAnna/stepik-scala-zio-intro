package ru.krivitskaya.anna.tasks.lesson2

sealed trait Value
case class IntValue(n: Int)       extends Value
case class StringValue(s: String) extends Value
case class BoolValue(b: Boolean)  extends Value

trait Decoder[A] { self => //TODO: variance of A
  def decode: PartialFunction[String, A]

  def orElse[B >: A](other: Decoder[B]): Decoder[B] =
    new Decoder[B] {
      val decode: PartialFunction[String, B] = self.decode.orElse(other.decode)
    }
}

object DecoderFactoryTask extends App {

  val boolDecoder: Decoder[BoolValue] = ???

  val intDecoder: Decoder[IntValue] = ???

  val stringDecoder: Decoder[StringValue] = ???

//  val decoders: List[Decoder[Value]] = List(boolDecoder, intDecoder, stringDecoder)
//
//  val valueDecoder: Decoder[Value] = decoders.reduce(_ orElse _)
//
//  val input = scala.io.StdIn.readLine()
//  println(valueDecoder.decode(input))
}
