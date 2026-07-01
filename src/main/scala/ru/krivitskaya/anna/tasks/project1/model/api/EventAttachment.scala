package ru.krivitskaya.anna.tasks.project1.model.api

import ru.krivitskaya.anna.tasks.project1.model.common.EventType.EventType

import java.util.UUID

case class EventAttachment(
    eventAttachmentId: UUID,
    eventType: EventType,
    eventDescription: Option[String]
)
