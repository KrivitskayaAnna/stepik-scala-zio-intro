package ru.krivitskaya.anna.tasks.lesson7

import zio._

import java.io.IOException

case class OrderRecord(id: Int, product: String, amount: Double)

object ImportOrdersTask extends ZIOAppDefault {
  val rawRecords: List[String] = List(
    "1,Laptop,999.99",
    "2,Phone,", // невалидная — нет суммы
    "3,Tablet,449.99",
    "abc,Mouse,29.99", // невалидная — id не число
    "5,Keyboard,79.99",
    "6,,149.99", // невалидная — нет названия продукта
    "7,Monitor,349.99"
  )

  // Реализуйте парсинг строки в OrderRecord. Если строка невалидна — вернуть ошибку с описанием проблемы.
  def parseRecord(raw: String): IO[String, OrderRecord] = ???

  // Реализуйте импорт: partition → логирование ошибок + обработка успешных → отчёт
  def importRecords(records: List[String]): IO[IOException, Unit] = ???

  override def run = importRecords(rawRecords)
}
