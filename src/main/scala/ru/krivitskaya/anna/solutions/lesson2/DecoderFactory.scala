package ru.krivitskaya.anna.solutions.lesson2

sealed trait Value
case class IntValue(n: Int) extends Value
case class StringValue(s: String) extends Value
case class BoolValue(b: Boolean) extends Value

trait Decoder[+A] { self =>
  def decode: PartialFunction[String, A]

  def orElse[B >: A](other: Decoder[B]): Decoder[B] =
    new Decoder[B] {
      val decode: PartialFunction[String, B] = self.decode.orElse(other.decode)
    }
}

object DecoderFactory extends App {
  val boolDecoder: Decoder[BoolValue] = new Decoder[BoolValue] {
    val decode: PartialFunction[String, BoolValue] = {
      case "true"  => BoolValue(true)
      case "false" => BoolValue(false)
    }
  }

  val intDecoder: Decoder[IntValue] = new Decoder[IntValue] {
    val decode: PartialFunction[String, IntValue] = {
      case s if s.toIntOption.isDefined => IntValue(s.toInt)
    }
  }

  val stringDecoder: Decoder[StringValue] = new Decoder[StringValue] {
    val decode: PartialFunction[String, StringValue] = {
      case s => StringValue(s)
    }
  }

  val decoders: List[Decoder[Value]] = List(boolDecoder, intDecoder, stringDecoder)

  val valueDecoder: Decoder[Value] = decoders.reduce(_ orElse _)

  val input = scala.io.StdIn.readLine()
  println(valueDecoder.decode(input))
}