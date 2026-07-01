package ru.krivitskaya.anna.tasks.project1.testdata

import ru.krivitskaya.anna.tasks.project1.model.api.SessionToken.SessionToken
import ru.krivitskaya.anna.tasks.project1.model.api._
import ru.krivitskaya.anna.tasks.project1.model.common.{EventType, ExternalConnection}

import java.util.UUID

object TestData {
  val apiConnection: ExternalConnection =
    ExternalConnection("https://api_host:port", "api_user", "api_pwd")

  val databaseConnection: ExternalConnection =
    ExternalConnection("https://db_host:port", "db_user", "db_pwd")

  val sessionToken: SessionToken =
    "YEW3BfOUv62DZcr9vmqtH7xN2-Vbw8FKswV3gvZdulEHFrqqJFvvzkAsXl070sGAizx1Zmn0FjyT9QFxbUc0aRN3vFhUMiGnYrB6_-g5zEM"

  val lastEventId: Long = 1000L

  val newEvents: List[Event] = List(
    Event(1001L, "2025-05-09 11:03:50"),
    Event(1004L, "2025-05-10 12:30:05"),
    Event(1007L, "2025-05-10 13:29:46"),
    Event(1008L, "2025-05-10 13:29:46")
  )

  val event1001Attachment: EventAttachment = EventAttachment(
    UUID.fromString("50646843-9cd9-4f25-9ee9-1121d698ba9a"),
    EventType.appEntry,
    None
  )

  val event1004Attachment: EventAttachment = EventAttachment(
    UUID.fromString("e1cec843-ecba-47fe-8675-723ca0ff96aa"),
    EventType.fraudCall,
    Some("client was called by fraud phone number")
  )

  val event1008Attachment: EventAttachment = EventAttachment(
    UUID.fromString("a99bfa14-0887-4eb0-927c-37dd6669aac6"),
    EventType.cardPayment,
    Some("client paid by card")
  )
}
