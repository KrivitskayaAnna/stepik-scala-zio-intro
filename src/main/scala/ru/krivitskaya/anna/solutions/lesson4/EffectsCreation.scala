package ru.krivitskaya.anna.solutions.lesson4

import zio._

import java.io.IOException

object EffectsCreation {
  // Чтение строки из консоли. Используйте готовый эффект из zio.Console
  def readInput: IO[IOException, String] = zio.Console.readLine

  // Парсинг строки в Int — может бросить NumberFormatException
  def parseInt(s: String): Task[Int] = ZIO.attempt(s.toInt)

  // Валидация: число должно быть положительным
  def validatePositive(n: Int): IO[String, Int] =
    if (n > 0) ZIO.succeed(n) else ZIO.fail("Number should be positive")

  // Удвоение числа — чистое вычисление, исключений быть не может
  def double(n: Int): UIO[Int] = ZIO.succeed(n * 2)
}