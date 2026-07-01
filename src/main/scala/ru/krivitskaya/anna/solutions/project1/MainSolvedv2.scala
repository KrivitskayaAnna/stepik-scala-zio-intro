package ru.krivitskaya.anna.solutions.project1

import ru.krivitskaya.anna.tasks.project1.clients._
import ru.krivitskaya.anna.tasks.project1.model.common.EventType
import ru.krivitskaya.anna.tasks.project1.model.database.EventEntity
import ru.krivitskaya.anna.tasks.project1.testdata.TestData._
import zio._

object MainSolvedv2 extends ZIOAppDefault {
  val programPart1v2 =
    (for {
      _         <- ZIO.logInfo("Прога запустилась")
      apiClient <- ZIO.service[ApiClient]
      token     <- apiClient.getSessionToken
    } yield ())
      .foldCauseZIO(
        cause => ZIO.logErrorCause("Ошибочка вышла", cause),
        success => ZIO.logInfo(s"Успех") *> programPart2
      )

  val programPart1 =
    for {
      _           <- ZIO.logInfo("Прога запустилась")
      apiClient   <- ZIO.service[ApiClient]
      eitherToken <- apiClient.getSessionToken.either
      _ <- eitherToken match {
        case Left(err) => ZIO.logError(s"Ошибка при получении токена ${err.getLocalizedMessage}")
        case Right(_)  => programPart2
      }
    } yield ()

  val programPart2: ZIO[DatabaseClient with ApiClient, Throwable, Unit] =
    for {
      apiClient   <- ZIO.service[ApiClient]
      dbClient    <- ZIO.service[DatabaseClient]
      lastEventId <- dbClient.getLastEventId
      newEvents   <- apiClient.getEventsNewerThan(lastEventId)
      _           <- ZIO.logInfo(s"Нет новых событий").when(newEvents.isEmpty)
      _ <- ZIO
        .logInfo(s"Есть новые события: ${newEvents.size} ${newEvents.map(_.eventId).mkString(",")}")
        .when(newEvents.nonEmpty)
      successAttachments <- ZIO
        .foreachPar(newEvents)(event =>
          apiClient
            .getEventAttachment(event.eventId)
            .mapBoth(err => (err, event), attachment => (attachment, event))
            .either
        )
        .map(_.partitionMap(identity))
        .withParallelism(3)
        .tap {
          case (err, _) =>
            val errWithEventId: List[(String, Long)] = err.map {
              case (err, event) => (err.getLocalizedMessage, event.eventId)
            }
            ZIO.logError(s"Ошибки получения вложений: ${errWithEventId.mkString(",")}")
        }
        .map { case (_, suc) => suc }
      (goodEvents, badEvents) = successAttachments.partition {
        case (att, _) => att.eventType != EventType.fraudCall
      }
      _ <- ZIO.logInfo(
        s"Отфильтровали ${badEvents.size} событий с id ${badEvents
          .map { case (_, event) => event.eventId }}"
      )
      _ <- ZIO.logInfo(
        s"Осталось ${goodEvents.size} событий с id ${goodEvents
          .map { case (_, event) => event.eventId }}"
      )
      goodEntities = goodEvents.map {
        case (att, event) =>
          EventEntity(
            eventId = event.eventId,
            publishedDate = event.publishedDate,
            eventAttachmentId = att.eventAttachmentId.toString,
            eventType = att.eventType.toString,
            eventDescription = att.eventDescription
          )
      }
      _ <- dbClient.batchSaveFilteredEvents(goodEntities)
      _ <- ZIO.logInfo("На это все")
      maxId = goodEntities.maxByOption(_.eventId).getOrElse(0)
      _ <- ZIO.logInfo(s"Максимальный id $maxId, ${goodEntities.size}")
      _ <- ZIO.sleep(2.seconds)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    programPart1v2.timed
      .tap { case (duration, _) => ZIO.logInfo(s"Обработка заняла ${duration.getSeconds} секунд") }
      .repeat(zio.Schedule.fixed(10.seconds))
      .provide(
        (ZLayer.succeed(apiConnection) >>> ApiClient.live),
        (ZLayer.succeed(databaseConnection) >>> DatabaseClient.live)
      )
}
