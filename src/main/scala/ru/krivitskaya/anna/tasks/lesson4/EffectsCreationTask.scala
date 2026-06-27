package ru.krivitskaya.anna.tasks.lesson4

import zio._

import java.io.IOException

object EffectsCreationTask {
  // Чтение строки из консоли. Используйте готовый эффект из zio.Console
  def readInput: IO[IOException, String] = ???

  // Парсинг строки в Int — может бросить NumberFormatException
  def parseInt(s: String): Task[Int] = ???

  // Валидация: число должно быть положительным
  def validatePositive(n: Int): IO[String, Int] = ???

  // Удвоение числа — чистое вычисление, исключений быть не может
  def double(n: Int): UIO[Int] = ???
}
