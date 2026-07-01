package ru.krivitskaya.anna.tasks.project1.model.common

object EventType extends Enumeration {
  type EventType = Value

  val fraudCall, appEntry, cardPayment = Value
}
