package ru.krivitskaya.anna.solutions.lesson10

import ru.krivitskaya.anna.solutions.lesson10.ServiceRegistry.serviceLayers
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
  val live: ULayer[IntService] = ZLayer.succeed(new IntService)

  def getProcessor: PartialFunction[String, RIO[IntService, Service]] = {
    case x if x == "int" => ZIO.service[IntService]
  }
}

object BooleanService extends Processor[BooleanService] {
  val live: ULayer[BooleanService] = ZLayer.succeed(new BooleanService)

  def getProcessor: PartialFunction[String, RIO[BooleanService, Service]] = {
    case x if x == "bool" => ZIO.service[BooleanService]
  }
}

object ServiceRegistry {
  private val servicesList = List(IntService, BooleanService)

  def chooseProcessorByInput(input: String) =
    servicesList.map(_.getProcessor).reduce(_.orElse(_))(input)

  val serviceLayers = IntService.live ++ BooleanService.live
}

object ZIOFactory extends ZIOAppDefault {
  private def callPipelineByInput(input: String) =
    ServiceRegistry.chooseProcessorByInput(input).flatMap(_.processPipeline)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    (callPipelineByInput("bool") *>
      callPipelineByInput("int") *>
      callPipelineByInput("unknown"))
      .provide(serviceLayers)
}
