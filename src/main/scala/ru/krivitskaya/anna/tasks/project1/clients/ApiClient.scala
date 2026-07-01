package ru.krivitskaya.anna.tasks.project1.clients

import ru.krivitskaya.anna.tasks.project1.model.api.SessionToken.SessionToken
import ru.krivitskaya.anna.tasks.project1.model.api.{Event, EventAttachment}
import ru.krivitskaya.anna.tasks.project1.model.common.ExternalConnection
import ru.krivitskaya.anna.tasks.project1.testdata.TestData._
import zio._

case class ApiClient(apiConnection: ExternalConnection) {
  def getSessionToken: Task[SessionToken] =
    ZIO.succeed(sessionToken) //замените на ZIO.fail(...) при тестировании сценария с ошибкой или рандомизируйте succeed и fail с помощью zio.Random.nextBoolean

  def getEventsNewerThan(eventId: Long): Task[List[Event]] =
    if (eventId == lastEventId) ZIO.succeed(newEvents) else ZIO.succeed(List.empty[Event]) //замените на ZIO.fail(...) при тестировании сценария с ошибкой

  def getEventAttachment(eventId: Long): Task[EventAttachment] =
    eventId match {
      case 1001L => ZIO.succeed(event1001Attachment)
      case 1004L => ZIO.succeed(event1004Attachment)
      case 1008L => ZIO.succeed(event1008Attachment)
      case _     => ZIO.fail(new RuntimeException(s"No attachment found for eventId $eventId"))
    }
}

object ApiClient {
  val live: ZLayer[ExternalConnection, Throwable, ApiClient] = ??? //TODO
}
