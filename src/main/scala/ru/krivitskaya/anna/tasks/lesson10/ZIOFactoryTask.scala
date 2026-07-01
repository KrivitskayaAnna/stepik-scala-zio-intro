package ru.krivitskaya.anna.tasks.lesson10

import zio._

import java.io.IOException

sealed trait Service {
  def processPipeline: IO[IOException, Unit]
}

class IntService extends Service {
  def processPipeline: IO[IOException, Unit] = zio.Console.printLine("Got int processor")
}

class BooleanService extends Service {
  def processPipeline: IO[IOException, Unit] = zio.Console.printLine("Got bool processor")
}

sealed trait Processor[-T] {
  def getProcessor: PartialFunction[String, RIO[T, Service]]
}

object IntService extends Processor[IntService] {
  // Создайте ULayer для IntService
  val live: ULayer[IntService] = ???

  // Реализуйте getProcessor: PartialFunction должна срабатывать на input "int"
  // и возвращать эффект, который достаёт IntService из окружения через ZIO.service
  def getProcessor: PartialFunction[String, RIO[IntService, Service]] = ???
}

object BooleanService extends Processor[BooleanService] {
  // Создайте ULayer для BooleanService
  val live: ULayer[BooleanService] = ???

  // Реализуйте getProcessor: PartialFunction должна срабатывать на input "bool"
  def getProcessor: PartialFunction[String, RIO[BooleanService, Service]] = ???
}

object ServiceRegistry {
  private val servicesList = List(IntService, BooleanService)

  // Скомбинируйте getProcessor всех процессоров в один через orElse (используйте servicesList) и примените к input
  // Замените Any на правильные зависимости (Any сейчас в качестве заглушки для того, чтобы код компилировался)
  def chooseProcessorByInput(input: String): RIO[Any, Service] = ???

  // Соберите единый слой зависимостей, которые вам необходимо передать для запуска программы
  val serviceLayers = ???
}

object ZIOFactoryTask extends ZIOAppDefault {
  private def callPipelineByInput(input: String) =
    ServiceRegistry.chooseProcessorByInput(input).flatMap(_.processPipeline)

  // Запустите пайплайн, передавая различные значения input: "bool", "int", "unknown". Предоставьте нужные слои через provide.
  // Перед тем как писать provide, посмотрите на выведенный тип R — какие зависимости он требует и почему?
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???
}
