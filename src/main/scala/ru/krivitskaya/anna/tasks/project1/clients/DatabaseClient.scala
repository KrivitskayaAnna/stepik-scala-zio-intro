package ru.krivitskaya.anna.tasks.project1.clients

import ru.krivitskaya.anna.tasks.project1.model.common.ExternalConnection
import ru.krivitskaya.anna.tasks.project1.model.database.EventEntity
import ru.krivitskaya.anna.tasks.project1.testdata.TestData.lastEventId
import zio._

import java.sql.SQLException

case class DatabaseClient(dbConnection: ExternalConnection) {
  def getLastEventId: IO[SQLException, Long] =
    ZIO
      .attempt(lastEventId)
      .refineToOrDie[SQLException] //замените на ZIO.fail(new SQLException(...)) при тестировании сценария с ошибкой

  def batchSaveFilteredEvents(eventEntities: List[EventEntity]): IO[SQLException, Unit] =
    ZIO.unit //замените на ZIO.fail(new SQLException(...)) при тестировании сценария с ошибкой
}

object DatabaseClient {
  val live = ??? //TODO
}
