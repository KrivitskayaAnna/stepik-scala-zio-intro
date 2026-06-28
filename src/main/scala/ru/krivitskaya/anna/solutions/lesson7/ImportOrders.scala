package ru.krivitskaya.anna.solutions.lesson7

import zio._

import java.io.IOException

case class OrderRecord(id: Int, product: String, amount: Double)

object ImportOrders extends ZIOAppDefault {
  val rawRecords: List[String] = List(
    "1,Laptop,999.99",
    "2,Phone,", // невалидная — нет суммы
    "3,Tablet,449.99",
    "abc,Mouse,29.99", // невалидная — id не число
    "5,Keyboard,79.99",
    "6,,149.99", // невалидная — нет названия продукта
    "7,Monitor,349.99"
  )

  // Реализуйте парсинг строки в OrderRecord. Если строка невалидна - вернуть ошибку с описанием проблемы.
  def parseRecord(raw: String): IO[String, OrderRecord] = {
    val parts = raw.split(",", -1)
    for {
      _       <- ZIO.fail(s"Expected 3 fields, got ${parts.length}").when(parts.length != 3).unit
      id      <- ZIO.attempt(parts(0).toInt).orElseFail("Failed to parse id")
      product <- if (parts(1).nonEmpty) ZIO.succeed(parts(1)) else ZIO.fail("Product name is empty")
      amount  <- ZIO.attempt(parts(2).toDouble).orElseFail("Failed to parse amount")
    } yield OrderRecord(id, product, amount)
  }

  // Реализуйте импорт: partition → логирование ошибок + обработка успешных → отчёт
  def importRecords(records: List[String]): IO[IOException, Unit] =
    for {
      (errors, successes) <- ZIO.partition(records) { raw =>
        parseRecord(raw).mapError(err => s""""$raw" - $err""")
      }
      _     <- ZIO.foreachParDiscard(errors)(err => ZIO.logWarning(s"Skipped: $err"))
      total <- ZIO.foldLeft(successes)(0.0)((acc, r) => ZIO.succeed(acc + r.amount))
      _ <- Console
        .printLine(
          s"Import complete: ${successes.size} imported, ${errors.size} skipped, total amount: $total"
        )
    } yield ()

  override def run = importRecords(rawRecords)
}
