package ru.krivitskaya.anna.solutions.project1

import ru.krivitskaya.anna.tasks.project1.clients.{ApiClient, DatabaseClient}
import ru.krivitskaya.anna.tasks.project1.model.common.EventType
import ru.krivitskaya.anna.tasks.project1.model.database.EventEntity
import ru.krivitskaya.anna.tasks.project1.testdata.TestData._
import zio._

case class ProgramConfig(
    launchIntervalMinutes: Int,
    sleepBeforeFinishSeconds: Int,
    programParallelism: Int,
    defaultMaxEventId: Long
)

object ProgramConfig {
  val live = ZLayer.succeed(
    ProgramConfig(
      launchIntervalMinutes = 1,
      sleepBeforeFinishSeconds = 10,
      programParallelism = 3,
      defaultMaxEventId = 0
    )
  )
}

object MainSolved extends ZIOAppDefault {

  val program: RIO[ProgramConfig with DatabaseClient with ApiClient, Unit] =
    for {
      _ <- ZIO.logInfo("Начало исполнения программы")
      token <- ZIO
        .serviceWithZIO[ApiClient](_.getSessionToken)
        .foldCauseZIO(
          cause => ZIO.logErrorCause("Не удалось получить токен", cause).as(None),
          success => ZIO.some(success)
        )
      _ <- getEvents.when(token.nonEmpty)
      _ <- ZIO.serviceWithZIO[ProgramConfig](config =>
        ZIO.sleep(config.sleepBeforeFinishSeconds.seconds)
      )
    } yield ()

  //TODO: разбить на более мелкие элементы в соотв с Single Responsibility Principle
  val getEvents: RIO[ProgramConfig with DatabaseClient with ApiClient, Unit] =
    for {
      apiClient     <- ZIO.service[ApiClient]
      dbClient      <- ZIO.service[DatabaseClient]
      programConfig <- ZIO.service[ProgramConfig]
      lastEventId   <- dbClient.getLastEventId
      newEvent      <- apiClient.getEventsNewerThan(lastEventId)
      _ <- if (newEvent.isEmpty) ZIO.logInfo("Нет новых событий, ждем следующий запуск")
      else
        ZIO.logInfo(
          s"Новых событий: ${newEvent.size}, id: ${newEvent.map(_.eventId).mkString(",")}"
        )
      attachments <- ZIO
        .foreachPar(newEvent) { event =>
          apiClient
            .getEventAttachment(event.eventId)
            .zip(ZIO.succeed(event))
            .mapError((_, event))
            .either
        }
        .withParallelism(programConfig.programParallelism)
        .map(_.partitionMap(identity))
        .tap {
          case (errors, _) =>
            ZIO.logError(s"Не удалось получить вложения для событий: ${errors
              .map { case (err, event) => (err.getLocalizedMessage, event.eventId) }}")
        }
        .map { case (_, successEvents) => successEvents }
      (fraudCallAttachments, filteredAttachments) = attachments.partition {
        case (attachment, _) =>
          attachment.eventType == EventType.fraudCall
      }
      _ <- ZIO.logInfo(
        s"Отфильтровано событий: ${fraudCallAttachments.size}, id: ${fraudCallAttachments.map(_._2.eventId).mkString(",")}"
      )
      _ <- ZIO.logInfo(
        s"Осталось событий: ${filteredAttachments.size}, id: ${filteredAttachments.map(_._2.eventId).mkString(",")}"
      )
      eventEntities = filteredAttachments.map {
        case (eventAttachment, event) =>
          EventEntity(
            event.eventId,
            event.publishedDate,
            eventAttachment.eventAttachmentId.toString,
            eventAttachment.eventType.toString,
            eventAttachment.eventDescription
          )
      }
      _ <- dbClient.batchSaveFilteredEvents(eventEntities)
      maxProcessedId = attachments
        .maxByOption { case (_, event) => event.eventId }
        .map { case (_, event) => event.eventId }
        .getOrElse(programConfig.defaultMaxEventId)
      _ <- ZIO.logInfo(
        s"Обработано событий: ${attachments.size}, последний успешно обработанный id: $maxProcessedId"
      )
    } yield ()

  val apiClientLayer: TaskLayer[ApiClient] = ZLayer.succeed(apiConnection) >>> ApiClient.live

  val dbClientLayer: TaskLayer[DatabaseClient] =
    ZLayer.succeed(databaseConnection) >>> DatabaseClient.live

  override def run: Task[Long] =
    ZIO
      .serviceWithZIO[ProgramConfig] { config =>
        program.timed
          .tap {
            case (duration, _) =>
              ZIO.logInfo(
                s"Завершение исполнения программы, исполнение заняло ${duration.toSeconds} секунд"
              )
          }
          .repeat(Schedule.fixed(config.launchIntervalMinutes.minutes))
      }
      .provide(ProgramConfig.live, apiClientLayer, dbClientLayer)
}
