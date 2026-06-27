package ru.krivitskaya.anna.tasks.lesson4

import zio._

import scala.util.Try

object LegacyIntegrationTask {
  // Legacy-функции — менять их не нужно
  def legacyFindUser(id: Int): Option[String] =
    if (id > 0) Some(s"User$id") else None

  def legacyGetAge(name: String): Either[String, Int] =
    if (name.nonEmpty) Right(name.length * 3) else Left("Empty name")

  def legacyFormatRecord(name: String, age: Int): Try[String] =
    Try(s"$name (age $age)")

  // На основе legacy-функций создайте ZIO-эффекты с осмысленными ошибками
  def findUser(id: Int): IO[String, String] = ???

  def getAge(name: String): IO[String, Int] = ???

  def formatRecord(name: String, age: Int): IO[String, String] = ???
}
