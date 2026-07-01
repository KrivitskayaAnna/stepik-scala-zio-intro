package ru.krivitskaya.anna.tasks.project1.model.database

case class EventEntity(
    eventId: Long,
    publishedDate: String,
    eventAttachmentId: String,
    eventType: String,
    eventDescription: Option[String]
)
